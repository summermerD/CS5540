package arch.Java3DErrorListener;

import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.j3d.Component3DManager;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;
import com.eteks.sweethome3d.model.HomeRecorder;
import com.eteks.sweethome3d.model.Library;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.plugin.PluginManager;
import com.eteks.sweethome3d.viewcontroller.ContentManager;
import com.eteks.sweethome3d.viewcontroller.View;

import arch.SweetHome3DMain.HomeFrameController;
import arch.SweetHome3DMain.SweetHome3DMainImp;

public class Java3DErrorListenerImp extends HomeApplication implements IJava3DErrorListenerImp
{
  private final Map<Home, HomeFrameController> homeFrameControllers;
  private ContentManager          contentManager;
  private HomeRecorder            homeRecorder;
  private UserPreferences         userPreferences;
  private static final String     PREFERENCES_FOLDER             = "com.eteks.sweethome3d.preferencesFolder";
  private static final String     APPLICATION_FOLDERS            = "com.eteks.sweethome3d.applicationFolders";
  private static final String     APPLICATION_PLUGINS_SUB_FOLDER = "plugins";
  private boolean                 pluginManagerInitialized;
  private PluginManager           pluginManager;

  private boolean                 checkUpdatesNeeded;

  /**
   * Returns the plugin manager of this application.
   */
  protected PluginManager getPluginManager() {
    if (!this.pluginManagerInitialized) {
      try {
        UserPreferences userPreferences = getUserPreferences();
        if (userPreferences instanceof FileUserPreferences) {
          File [] applicationPluginsFolders = ((FileUserPreferences) userPreferences)
              .getApplicationSubfolders(APPLICATION_PLUGINS_SUB_FOLDER);
          // Create the plug-in manager that will search plug-in files in plugins folders
          this.pluginManager = new PluginManager(applicationPluginsFolders);
        }
      } catch (IOException ex) {
      }
      this.pluginManagerInitialized = true;
    }
    return this.pluginManager;
  }
  

  /**
   * Returns a recorder able to write and read homes in files.
   */
  @Override
  public HomeRecorder getHomeRecorder() {
    // Initialize homeRecorder lazily
    if (this.homeRecorder == null) {
      this.homeRecorder = new HomeFileRecorder(0, false, getUserPreferences(), false);
    }
    return this.homeRecorder;
  }


  /**
   * Returns user preferences stored in resources and local file system.
   */
  @Override
  public UserPreferences getUserPreferences() {
    // Initialize userPreferences lazily
    if (this.userPreferences == null) {
      // Retrieve preferences and application folders
      String preferencesFolderProperty = System.getProperty(PREFERENCES_FOLDER, null);
      String applicationFoldersProperty = System.getProperty(APPLICATION_FOLDERS, null);
      File preferencesFolder = preferencesFolderProperty != null
          ? new File(preferencesFolderProperty)
          : null;
      File [] applicationFolders;
      if (applicationFoldersProperty != null) {
        String [] applicationFoldersProperties = applicationFoldersProperty.split(File.pathSeparator);
        applicationFolders = new File [applicationFoldersProperties.length];
        for (int i = 0; i < applicationFolders.length; i++) {
          applicationFolders [i] = new File(applicationFoldersProperties [i]);
        }
      } else {
        applicationFolders = null;
      }
      Executor eventQueueExecutor = new Executor() {
          public void execute(Runnable command) {
            EventQueue.invokeLater(command);
          }
        };
      this.userPreferences = new FileUserPreferences(preferencesFolder, applicationFolders, eventQueueExecutor) {
          @Override
          public List<Library> getLibraries() {
            if (userPreferences != null // Don't go further if preferences are not ready
                && getPluginManager() != null) {
              List<Library> pluginLibraries = getPluginManager().getPluginLibraries();
              if (!pluginLibraries.isEmpty()) {
                // Add plug-ins to the list returned by user preferences
                ArrayList<Library> libraries = new ArrayList<Library>(super.getLibraries());
                libraries.addAll(pluginLibraries);
                return Collections.unmodifiableList(libraries);
              }
            }
            return super.getLibraries();
          }
          
          @Override
          public void deleteLibraries(List<Library> libraries) throws RecorderException {
            if (userPreferences != null // Don't go further if preferences are not ready
                && getPluginManager() != null) {
              super.deleteLibraries(libraries);
              List<Library> plugins = new ArrayList<Library>();
              for (Library library : libraries) {
                if (PluginManager.PLUGIN_LIBRARY_TYPE.equals(library.getType())) {
                  plugins.add(library);
                }
              }
              getPluginManager().deletePlugins(plugins);
            }
          }
        };
      this.checkUpdatesNeeded = this.userPreferences.isCheckUpdatesEnabled();
    }
    return this.userPreferences;
  }
  
  /**
   * Displays in a 3D error message.
   */
  private void show3DError() {
    UserPreferences userPreferences = getUserPreferences();
    String message = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "3DError.message");
    String title = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "3DError.title");
    JOptionPane.showMessageDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(), message,
        title, JOptionPane.ERROR_MESSAGE);
  }
  /**
   * Displays a dialog that let user choose whether he wants to save modified
   * homes after an error in 3D rendering system.
   * @return <code>true</code> if user confirmed to save.
   */
  private boolean confirmSaveAfter3DError() {
    UserPreferences userPreferences = getUserPreferences();
    String message = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "confirmSaveAfter3DError.message");
    String title = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "confirmSaveAfter3DError.title");
    String save = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "confirmSaveAfter3DError.save");
    String doNotSave = userPreferences.getLocalizedString(SweetHome3DMainImp.class, "confirmSaveAfter3DError.doNotSave");

    return JOptionPane.showOptionDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
        message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object [] {save, doNotSave},
        save) == JOptionPane.YES_OPTION;
  }

  /**
   * Returns the controller of the given <code>home</code>.
   */
  public HomeFrameController getHomeFrameController(Home home) {
    return this.homeFrameControllers.get(home);
  }
  /**
   * Returns the frame that displays the given <code>home</code>.
   */
  public JFrame getHomeFrame(Home home) {
    return (JFrame)SwingUtilities.getRoot((JComponent)this.homeFrameControllers.get(home).getView());
  }
  /**
   * Displays a message to user about a 3D error, saves modified homes and
   * forces exit.
   */
  private void exitAfter3DError() {
    // Check if there are modified homes
    boolean modifiedHomes = false;
    for (Home home : getHomes()) {
      if (home.isModified()) {
        modifiedHomes = true;
        break;
      }
    }

    if (!modifiedHomes) {
      // Show 3D error message
      show3DError();
    } else if (confirmSaveAfter3DError()) {
      // Delete all homes after saving modified ones
      for (Home home : getHomes()) {
        if (home.isModified()) {
          String homeName = home.getName();
          if (homeName == null) {
            JFrame homeFrame = getHomeFrame(home);
            homeFrame.toFront();
            homeName = contentManager.showSaveDialog((View) homeFrame.getRootPane(), null,
                ContentManager.ContentType.SWEET_HOME_3D, null);
          }
          if (homeName != null) {
            try {
              // Write home with application recorder
              getHomeRecorder().writeHome(home, homeName);
            } catch (RecorderException ex) {
              // As this is an emergency exit, don't report error
              ex.printStackTrace();
            }
          }
          deleteHome(home);
        }
      }
    }
    // Close homes
    for (Home home : getHomes()) {
      deleteHome(home);
    }
    // Force exit if program didn't exit by itself
    System.exit(0);
  }
  
	private Java3DErrorListenerArch _arch;

    public Java3DErrorListenerImp (){
      this.homeFrameControllers = new HashMap<Home, HomeFrameController>();

    }

	public void setArch(Java3DErrorListenerArch arch){
		_arch = arch;
	}
	public Java3DErrorListenerArch getArch(){
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
  
    
    public void addComponent3DRenderingErrorObserver ()   {
		//TODO Auto-generated method stub
      if (!Boolean.getBoolean("com.eteks.sweethome3d.no3D")) { 
        // Add a RenderingErrorObserver to Component3DManager, because offscreen
        // rendering needs to check rendering errors with its own RenderingErrorListener
        Component3DManager.getInstance().setRenderingErrorObserver(new Component3DManager.RenderingErrorObserver() {
            public void errorOccured(int errorCode, String errorMessage) {
              System.err.print("Error in Java 3D : " + errorCode + " " + errorMessage);
              EventQueue.invokeLater(new Runnable() {
                public void run() {
                  exitAfter3DError();
                }
              });
            }
          });
      }
    }
}