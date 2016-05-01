package arch.BuildHome;

import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.eteks.sweethome3d.io.AutoRecoveryManager;
import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.CollectionEvent;
import com.eteks.sweethome3d.model.CollectionListener;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;
import com.eteks.sweethome3d.model.HomeRecorder;
import com.eteks.sweethome3d.model.Library;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.plugin.HomePluginController;
import com.eteks.sweethome3d.plugin.PluginManager;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.eteks.sweethome3d.viewcontroller.ContentManager;
import com.eteks.sweethome3d.viewcontroller.HomeController;
import com.eteks.sweethome3d.viewcontroller.View;
import com.eteks.sweethome3d.viewcontroller.ViewFactory;
import com.eteks.sweethome3d.viewcontroller.ContentManager.ContentType;

import arch.SweetHome3DMain.HomeFrameController;
import arch.SweetHome3DMain.SweetHome3DMainImp;

public class BuildHomeImp extends HomeApplication implements IBuildHomeImp
{
  private static final String     PREFERENCES_FOLDER             = "com.eteks.sweethome3d.preferencesFolder";
  private static final String     APPLICATION_FOLDERS            = "com.eteks.sweethome3d.applicationFolders";
  private static final String     APPLICATION_PLUGINS_SUB_FOLDER = "plugins";
  private HomeRecorder            homeRecorder;
  private HomeRecorder            compressedHomeRecorder;
  private UserPreferences         userPreferences;
  private boolean                 pluginManagerInitialized;
  private PluginManager           pluginManager;
  private boolean                 checkUpdatesNeeded;
  private final Map<Home, HomeFrameController> homeFrameControllers;
  private ViewFactory             viewFactory;
  private ContentManager          contentManager;
  private AutoRecoveryManager     autoRecoveryManager;

  
  /**
   * Adds a listener to new home to close it if an other one is opened.
   */
  private void addNewHomeCloseListener(final Home home, final HomeController controller) {
    if (home.getName() == null) {
      final CollectionListener<Home> newHomeListener = new CollectionListener<Home>() {
        public void collectionChanged(CollectionEvent<Home> ev) {
          // Close new home for any named home added to application
          if (ev.getType() == CollectionEvent.Type.ADD) {
            if (ev.getItem().getName() != null 
                && home.getName() == null
                && !home.isRecovered()) {
              if (OperatingSystem.isMacOSXLionOrSuperior()
                  && OperatingSystem.isJavaVersionGreaterOrEqual("1.7")
                  && _arch.OUT_BuildHomeToMacConfig.isWindowFullScreen(getHomeFrame(home))) {
                // Delay home disposal to avoid Java 3D fatal error
                new Timer(3000, new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                      ((Timer)ev.getSource()).stop();
                      controller.close();
                    }
                  }).start();
              } else {
                controller.close();
              }
            }
            removeHomesListener(this);
          } else if (ev.getItem() == home && ev.getType() == CollectionEvent.Type.DELETE) {
            removeHomesListener(this);
          }
        }
      };
      addHomesListener(newHomeListener);
      // Disable this listener at first home change
      home.addPropertyChangeListener(Home.Property.MODIFIED, new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
          removeHomesListener(newHomeListener);
          home.removePropertyChangeListener(Home.Property.MODIFIED, this);
        }
      });
    }
  }

  /**
   * Returns the controller of the given <code>home</code>.
   */
  public HomeFrameController getHomeFrameController(Home home) {
    //return this.homeFrameControllers.get(home);
    return _arch.OUT_StartToBuildHome.GetStart(home);
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
   * Check updates if needed.
   */
  private void checkUpdates() {
    if (this.checkUpdatesNeeded) {
      this.checkUpdatesNeeded = false;
      // Delay updates checking to let program launch finish
      new Timer(500, new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            ((Timer)ev.getSource()).stop();
            // Check updates with a dummy controller
            createHomeFrameController(createHome()).getHomeController().checkUpdates(true);
          }
        }).start();
    }
  }

  /**
   * Shows and brings to front <code>home</code> frame.
   */
  private void showHomeFrame(Home home) {
    final JFrame homeFrame = getHomeFrame(home);
    homeFrame.setVisible(true);
    homeFrame.setState(JFrame.NORMAL);
    homeFrame.toFront();
  }
 
  /**
   * Returns the frame that displays the given <code>home</code>.
   */
  public JFrame getHomeFrame(Home home) {
    return (JFrame)SwingUtilities.getRoot((JComponent)this.homeFrameControllers.get(home).getView());
  }
  
  public HomeFrameController createHomeFrameController(Home home) {
    return new HomeFrameController(home, this, getViewFactory(), getContentManager(), getPluginManager());
  }
  
  /**
   * Returns a Swing view factory.
   */
  protected ViewFactory getViewFactory() {
    if (this.viewFactory == null) {
      this.viewFactory = _arch.OUT_GetViewFactoryInt.GetSwingViewFactory();
      
    }
    return this.viewFactory;
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
   * A file content manager that records the last directories for each content
   * in Java preferences.
   */
  private static class FileContentManagerWithRecordedLastDirectories extends FileContentManager {
    private static final String LAST_DIRECTORY         = "lastDirectory#";
    private static final String LAST_DEFAULT_DIRECTORY = "lastDefaultDirectory";
    
    private final Class<? extends BuildHomeImp> mainClass;

    public FileContentManagerWithRecordedLastDirectories(UserPreferences preferences, 
                                                         Class<? extends BuildHomeImp> mainClass) {
      super(preferences);
      this.mainClass = mainClass;
    }

    @Override
    protected File getLastDirectory(ContentType contentType) {
      Preferences preferences = Preferences.userNodeForPackage(this.mainClass);
      String directoryPath = null;
      if (contentType != null) {
        directoryPath = preferences.get(LAST_DIRECTORY + contentType, null);
      }
      if (directoryPath == null) {
        directoryPath = preferences.get(LAST_DEFAULT_DIRECTORY, null);
      }
      if (directoryPath != null) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
          return directory;
        } 
      }
      return null;
    }
    
    @Override
    protected void setLastDirectory(ContentType contentType, File directory) {
      // Last directories are not recorded in user preferences since there's no need of portability 
      // from a computer to an other
      Preferences preferences = Preferences.userNodeForPackage(this.mainClass);
      if (directory == null) {
        preferences.remove(LAST_DIRECTORY + contentType);
      } else {
        String directoryPath = directory.getAbsolutePath();
        if (contentType != null) {
          preferences.put(LAST_DIRECTORY + contentType, directoryPath);
        }
        if (directoryPath != null) {
          preferences.put(LAST_DEFAULT_DIRECTORY, directoryPath);
        }
      }
      try {
        preferences.flush();
      } catch (BackingStoreException ex) {
        // Ignore exception, Sweet Home 3D will work without recorded directories
      }
    }
  }
  
  /**
   * Shows a home frame, either a new one when no home is opened, or the last created home frame.  
   */
  private void showDefaultHomeFrame() {
    if (getHomes().isEmpty()) {
      if (this.autoRecoveryManager != null) {
        this.autoRecoveryManager.openRecoveredHomes();
      }
      if (getHomes().isEmpty()) {
        // Add a new home to application
        addHome(createHome());
      }
    } else {
      // If no Sweet Home 3D frame has focus, bring last created viewed frame to front
      final List<Home> homes = getHomes();
      Home home = null;
      for (int i = homes.size() - 1; i >= 0; i--) {
        JFrame homeFrame = getHomeFrame(homes.get(i));
        if (homeFrame.isActive() || homeFrame.getState() != JFrame.ICONIFIED) {
          home = homes.get(i);
          break;
        }
      }
      // If no frame is visible and not iconified, take any displayable frame
      if (home == null) {
        for (int i = homes.size() - 1; i >= 0; i--) {
          JFrame homeFrame = getHomeFrame(homes.get(i));
          if (homeFrame.isDisplayable()) {
            home = homes.get(i);
            break;
          }
        }
      }
 
      showHomeFrame(home);
    }
  }
  /**
   * Returns a content manager able to handle files.
   */
  protected ContentManager getContentManager() {
    if (this.contentManager == null) {
      this.contentManager = new FileContentManagerWithRecordedLastDirectories(getUserPreferences(), getClass());
    }
    return this.contentManager;
  }
  
  /**
   * Returns a recorder able to write and read homes in files.
   */
  @Override
  public HomeRecorder getHomeRecorder() {
    // Initialize homeRecorder lazily
    if (this.homeRecorder == null) {
      this.homeRecorder = _arch.OUT_HomeRecorderInterface.GetHomeRecorder();
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
   * Starts application once initialized and opens home passed in arguments. 
   * This method is executed from Event Dispatch Thread.
   */
  public void start(String [] args) {
    if (args.length == 2 && args [0].equals("-open") && args [1].length() > 0) {
      // If requested home is already opened, show it
      for (Home home : getHomes()) {
        if (args [1].equals(home.getName())) {
          showHomeFrame(home);
          return;
        }
      }
      
      if (getContentManager().isAcceptable(args [1], ContentManager.ContentType.SWEET_HOME_3D)) {
        // Add a listener to application to recover homes once the one in parameter is open
        addHomesListener(new CollectionListener<Home>() {
            public void collectionChanged(CollectionEvent<Home> ev) {
              if (ev.getType() == CollectionEvent.Type.ADD) {
                removeHomesListener(this);
                if (autoRecoveryManager != null) {
                  autoRecoveryManager.openRecoveredHomes();
                }
              }
            }
          });
        // Read home file in args [1] if args [0] == "-open" with a dummy controller
        createHomeFrameController(createHome()).getHomeController().open(args [1]);
        checkUpdates();
      } else if (getContentManager().isAcceptable(args [1], ContentManager.ContentType.LANGUAGE_LIBRARY)) {
        showDefaultHomeFrame();
        final String languageLibraryName = args [1];
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            List<String> supportedLanguages = Arrays.asList(getUserPreferences().getSupportedLanguages());
            // Import language library with a dummy controller
            createHomeFrameController(createHome()).getHomeController().importLanguageLibrary(languageLibraryName);
            // Switch to the first language added to supported languages
            for (String language : getUserPreferences().getSupportedLanguages()) {
              if (!supportedLanguages.contains(language)) {
                getUserPreferences().setLanguage(language);
                break;
              }
            }
            checkUpdates();
          }
        });
      } else if (getContentManager().isAcceptable(args [1], ContentManager.ContentType.FURNITURE_LIBRARY)) {
        showDefaultHomeFrame();
        final String furnitureLibraryName = args [1];
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            // Import furniture library with a dummy controller
            createHomeFrameController(createHome()).getHomeController().importFurnitureLibrary(furnitureLibraryName);
            checkUpdates();
          }
        });
      } else if (getContentManager().isAcceptable(args [1], ContentManager.ContentType.TEXTURES_LIBRARY)) {
        showDefaultHomeFrame();
        final String texturesLibraryName = args [1];
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            // Import textures library with a dummy controller
            createHomeFrameController(createHome()).getHomeController().importTexturesLibrary(texturesLibraryName);
            checkUpdates();
          }
        });
      } else if (getContentManager().isAcceptable(args [1], ContentManager.ContentType.PLUGIN)) {
        showDefaultHomeFrame();
        final String pluginName = args [1];
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            // Import plug-in with a dummy controller
            HomeController homeController = _arch.OUT_HomeControllerToBuildHome.GetHomeController();
            if (homeController instanceof HomePluginController) {
              ((HomePluginController)homeController).importPlugin(pluginName);
            }
            checkUpdates();
          }
        });
      }
    } else { 
      showDefaultHomeFrame();
      checkUpdates();
    }
  }
  
	private BuildHomeArch _arch;

    public BuildHomeImp (){
      this.homeFrameControllers = new HashMap<Home, HomeFrameController>();

    }

	public void setArch(BuildHomeArch arch){
		_arch = arch;
	}
	public BuildHomeArch getArch(){
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
  
    
    public void buildHome ()   {
		//TODO Auto-generated method stub
      SingleInstanceService service = null;
      final SingleInstanceListener singleInstanceListener = new SingleInstanceListener() {
        public void newActivation(final String [] args) {
          // Call run with the arguments it should have received
          EventQueue.invokeLater(new Runnable() {
            public void run() {
             start(args);
            }
          });
        }
      };
      try {
        // Retrieve Java Web Start SingleInstanceService
        service = (SingleInstanceService) ServiceManager.lookup("javax.jnlp.SingleInstanceService");
        service.addSingleInstanceListener(singleInstanceListener);
      } catch (UnavailableServiceException ex) {
        // Just ignore SingleInstanceService if it's not available
        // to let application work outside of Java Web Start
      }

      // Make a final copy of service
      final SingleInstanceService singleInstanceService = service;
    

    // Add a listener that opens a frame when a home is added to application
    addHomesListener(new CollectionListener<Home>() {
      private boolean firstApplicationHomeAdded;

      public void collectionChanged(CollectionEvent<Home> ev) {
        switch (ev.getType()) {
          case ADD:
            Home home = ev.getItem();
            try {
              HomeFrameController controller = createHomeFrameController(home);
              controller.displayView();
              if (!this.firstApplicationHomeAdded) {
                this.firstApplicationHomeAdded = true;
                addNewHomeCloseListener(home, controller.getHomeController());
              }

              homeFrameControllers.put(home, controller);
            } catch (IllegalStateException ex) {
              // Check exception by class name to avoid a mandatory bind to Java 3D
              if ("javax.media.j3d.IllegalRenderingStateException".equals(ex.getClass().getName())) {
                ex.printStackTrace();
                // In case of a problem in Java 3D, simply exit with a message.
                exitAfter3DError();
              } else {
                throw ex;
              }
            }
            break;
          case DELETE:
            homeFrameControllers.remove(ev.getItem());

            // If application has no more home
            if (getHomes().isEmpty() && !OperatingSystem.isMacOSX()) {
              // If SingleInstanceService is available, remove the listener that was added on it
              if (singleInstanceService != null) {
                singleInstanceService.removeSingleInstanceListener(singleInstanceListener);
              }
              // Exit once current events are managed (under Mac OS X, exit is managed by MacOSXConfiguration)
              EventQueue.invokeLater(new Runnable() {
                  public void run() {
                    System.exit(0);
                  }
                });
            }
            break;
        }
      };
    });
    }
    
    @Override
    public Home createHome() {
      return _arch.OUT_HomeInterface.GetHome();
    }
}