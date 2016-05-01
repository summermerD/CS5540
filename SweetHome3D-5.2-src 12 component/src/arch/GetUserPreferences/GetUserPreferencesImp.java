package arch.GetUserPreferences;


import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.model.Library;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.plugin.PluginManager;

public class GetUserPreferencesImp implements IGetUserPreferencesImp
{
  private UserPreferences         userPreferences;
  private boolean                 checkUpdatesNeeded;
  private boolean                 pluginManagerInitialized;
  private static final String     APPLICATION_PLUGINS_SUB_FOLDER = "plugins";
  private PluginManager           pluginManager;

  private static final String     PREFERENCES_FOLDER             = "com.eteks.sweethome3d.preferencesFolder";
  private static final String     APPLICATION_FOLDERS            = "com.eteks.sweethome3d.applicationFolders";
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
  
  private GetUserPreferencesArch _arch;

    public GetUserPreferencesImp (){
    }

	public void setArch(GetUserPreferencesArch arch){
		_arch = arch;
	}
	public GetUserPreferencesArch getArch(){
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
  
    //To be imported: UserPreferences
    public UserPreferences getUserPreferences ()   {
		//TODO Auto-generated method stub
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
}