package arch.SystemInitialization;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.ServiceManagerStub;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;

import com.eteks.sweethome3d.swing.SwingTools;
import com.eteks.sweethome3d.tools.OperatingSystem;

import arch.SweetHome3DMain.SweetHome3DMainImp;



public class SystemInitializationImp implements ISystemInitializationImp
{
  /**
   * A single instance service server that waits for further Sweet Home 3D
   * launches.
   */
  private static class StandaloneSingleInstanceService implements SingleInstanceService {
    private static final String                SINGLE_INSTANCE_PORT    = "singleInstancePort";

    private final Class<? extends SystemInitializationImp> mainClass;
    private final List<SingleInstanceListener> singleInstanceListeners = new ArrayList<SingleInstanceListener>();

    public StandaloneSingleInstanceService(Class<? extends SystemInitializationImp> mainClass) {
      this.mainClass = mainClass;
    }

    public void addSingleInstanceListener(SingleInstanceListener l) {
      if (this.singleInstanceListeners.isEmpty()) {
        if (!OperatingSystem.isMacOSX()) {
          // Launching a server is useless under Mac OS X because further launches will be notified 
          // by com.apple.eawt.ApplicationListener added to application in MacOSXConfiguration class
          launchSingleInstanceServer();
        }
      }
      this.singleInstanceListeners.add(l);
    }

    /**
     * Launches single instance server.
     */
    private void launchSingleInstanceServer() {
      final ServerSocket serverSocket;
      try {
        // Launch a server that waits for other Sweet Home 3D launches
        serverSocket = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"));
        // Share server port in preferences
        Preferences preferences = Preferences.userNodeForPackage(this.mainClass);
        preferences.putInt(SINGLE_INSTANCE_PORT, serverSocket.getLocalPort());
        preferences.flush();
      } catch (IOException ex) {
        // Ignore exception, Sweet Home 3D will work with multiple instances
        return;
      } catch (BackingStoreException ex) {
        // Ignore exception, Sweet Home 3D will work with multiple instances
        return;
      }

      Executors.newSingleThreadExecutor().execute(new Runnable() {
        public void run() {
          try {
            while (true) {
              // Wait client calls
              Socket socket = serverSocket.accept();
              // Read client params
              BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
              String [] params = reader.readLine().split("\t");
              reader.close();
              socket.close();

              // Work on a copy of singleInstanceListeners to ensure a listener
              // can modify safely listeners list
              SingleInstanceListener [] listeners = singleInstanceListeners
                  .toArray(new SingleInstanceListener [singleInstanceListeners.size()]);
              // Call listeners with received params
              for (SingleInstanceListener listener : listeners) {
                listener.newActivation(params);
              }
            }
          } catch (IOException ex) {
            // In case of problem, relaunch server
            launchSingleInstanceServer();
          }
        }
      });
    }

    public void removeSingleInstanceListener(SingleInstanceListener l) {
      this.singleInstanceListeners.remove(l);
      if (this.singleInstanceListeners.isEmpty()) {
        Preferences preferences = Preferences.userNodeForPackage(this.mainClass);
        preferences.remove(SINGLE_INSTANCE_PORT);
        try {
          preferences.flush();
        } catch (BackingStoreException ex) {
          throw new RuntimeException(ex);
        }
      }
    }

    /**
     * Returns <code>true</code> if single instance server was successfully
     * called.
     */
    public static boolean callSingleInstanceServer(String [] mainArgs, Class<? extends SystemInitializationImp> mainClass) {
      if (!OperatingSystem.isMacOSX()) {
        // No server under Mac OS X, multiple application launches are managed
        // by com.apple.eawt.ApplicationListener in MacOSXConfiguration class
        Preferences preferences = Preferences.userNodeForPackage(mainClass);
        int singleInstancePort = preferences.getInt(SINGLE_INSTANCE_PORT, -1);
        if (singleInstancePort != -1) {
          try {
            // Try to connect to single instance server
            Socket socket = new Socket("127.0.0.1", singleInstancePort);
            // Write main args
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            for (String arg : mainArgs) {
              writer.write(arg);
              writer.write("\t");
            }
            writer.write("\n");
            writer.close();
            socket.close();
            return true;
          } catch (IOException ex) {
            // Return false
          }
        }
      }
      return false;
    }
  }
  
  
  /**
   * <code>BasicService</code> that launches web browser either with Java SE 6
   * <code>java.awt.Desktop</code> class, or with the <code>open</code> command
   * under Mac OS X.
   */
  private static class StandaloneBasicService implements BasicService {
    public boolean showDocument(URL url) {
      try {
        if (OperatingSystem.isJavaVersionGreaterOrEqual("1.6")) {
          // Call Java SE 6 java.awt.Desktop browse method by reflection to
          // ensure Java SE 5 compatibility
          Class<?> desktopClass = Class.forName("java.awt.Desktop");
          Object desktopInstance = desktopClass.getMethod("getDesktop").invoke(null);
          desktopClass.getMethod("browse", URI.class).invoke(desktopInstance, url.toURI());
          return true;
        }
      } catch (Exception ex) {
        try {
          if (OperatingSystem.isMacOSX()) {
            Runtime.getRuntime().exec(new String [] {"open", url.toString()});
            return true;
          } else if (OperatingSystem.isLinux()) {
            Runtime.getRuntime().exec(new String [] {"xdg-open", url.toString()});
            return true;
          }  
        } catch (IOException ex2) {
        }
        // For other cases, let's consider simply the showDocument method failed
      }
      return false;
    }

    public URL getCodeBase() {
      // Return a default URL matching the <code>resources</code> sub directory.
      return StandaloneServiceManager.class.getResource("resources");
    }

    public boolean isOffline() {
      return false;
    }

    public boolean isWebBrowserSupported() {
      if (OperatingSystem.isJavaVersionGreaterOrEqual("1.6")) {
        try {
          // Call Java SE 6 java.awt.Desktop isSupported(Desktop.Action.BROWSE)
          // method by reflection to ensure Java SE 5 compatibility
          Class<?> desktopClass = Class.forName("java.awt.Desktop");
          Object desktopInstance = desktopClass.getMethod("getDesktop").invoke(null);
          Class<?> desktopActionClass = Class.forName("java.awt.Desktop$Action");
          Object desktopBrowseAction = desktopActionClass.getMethod("valueOf", String.class).invoke(null, "BROWSE");
          if ((Boolean)desktopClass.getMethod("isSupported", desktopActionClass).invoke(desktopInstance,
              desktopBrowseAction)) {
            return true;
          }
        } catch (Exception ex) {
          // For any exception, let's consider simply the isSupported method failed
        }
      }
      // For other Java versions, let's support Mac OS X and Linux
      return OperatingSystem.isMacOSX() || OperatingSystem.isLinux();
    }
  }
  
  /**
   * JNLP <code>ServiceManagerStub</code> implementation for standalone
   * applications run out of Java Web Start. This service manager supports
   * <code>BasicService</code> and <code>javax.jnlp.SingleInstanceService</code>.
   * .
   */
  private static class StandaloneServiceManager implements ServiceManagerStub {
    private final Class<? extends SystemInitializationImp> mainClass;

    public StandaloneServiceManager(Class<? extends SystemInitializationImp> mainClass) {
      this.mainClass = mainClass;
    }

    public Object lookup(final String name) throws UnavailableServiceException {
      if (name.equals("javax.jnlp.BasicService")) {
        // Create a basic service that uses Java SE 6 java.awt.Desktop class
        return new StandaloneBasicService();
      } else if (name.equals("javax.jnlp.SingleInstanceService")) {
        // Create a server that waits for further Sweet Home 3D launches
        return new StandaloneSingleInstanceService(this.mainClass);
      } else {
        throw new UnavailableServiceException(name);
      }
    }

    public String [] getServiceNames() {
      return new String [] {"javax.jnlp.BasicService", "javax.jnlp.SingleInstanceService"};
    }
  }
	private SystemInitializationArch _arch;

    public SystemInitializationImp (){
    }

	public void setArch(SystemInitializationArch arch){
		_arch = arch;
	}
	public SystemInitializationArch getArch(){
		return _arch;
	}

	/*
  	  Myx Lifecycle Methods: these methods are called automatically by the framework
  	  as the bricks are created, attached, detached, and destroyed respectively.
	*/	
	public void init(){
	    //TODO Auto-generated method stub
	}
	public void begin(){
		//TODO Auto-generated method stub
	}
	public void end(){
		//TODO Auto-generated method stub
	}
	public void destroy(){
		//TODO Auto-generated method stub
	}

	/*
  	  Implementation primitives required by the architecture
	*/
  
    
    public void initialization (final String [] args)   {
		//TODO Auto-generated method stub
      if (OperatingSystem.isMacOSX()) {
        // Change Mac OS X application menu name
        String classPackage = SweetHome3DMainImp.class.getName();
        classPackage = classPackage.substring(0, classPackage.lastIndexOf("."));
        ResourceBundle resource = ResourceBundle.getBundle(classPackage + "." + "package");
        String applicationName = resource.getString("SweetHome3D.applicationName");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);
        System.setProperty("apple.awt.application.name", applicationName);
        // Use Mac OS X screen menu bar for frames menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        // Force the use of Quartz under Mac OS X for better Java 2D rendering performance
        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        if (System.getProperty("com.eteks.sweethome3d.dragAndDropWithoutTransferHandler") == null
            && OperatingSystem.isJavaVersionBetween("1.7", "1.8.0_40")) {
          System.setProperty("com.eteks.sweethome3d.dragAndDropWithoutTransferHandler", "true");
        }
      }
      // Request to use system proxies to access to the Internet
      if (System.getProperty("java.net.useSystemProxies") == null) {
        System.setProperty("java.net.useSystemProxies", "true");
      }
   // If Sweet Home 3D is launched from outside of Java Web Start
      if (ServiceManager.getServiceNames() == null) {
        // Try to call single instance server
        if (StandaloneSingleInstanceService.callSingleInstanceServer(args, getClass())) {
          // If single instance server was successfully called, exit application
          System.exit(0);
        } else {
          // Display splash screen
          SwingTools.showSplashScreenWindow(SweetHome3DMainImp.class.getResource("resources/splashScreen.jpg"));
          // Create JNLP services required by Sweet Home 3D
          ServiceManager.setServiceManagerStub(new StandaloneServiceManager(getClass()));
        }
      }
    }


}