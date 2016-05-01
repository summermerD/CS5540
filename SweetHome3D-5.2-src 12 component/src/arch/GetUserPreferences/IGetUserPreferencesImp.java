package arch.GetUserPreferences;


import arch.GetUserPreferences.GetUserPreferencesArch;

import com.eteks.sweethome3d.model.UserPreferences;

public interface IGetUserPreferencesImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (GetUserPreferencesArch arch);
	public GetUserPreferencesArch getArch();
	
	/*
  	  Myx Lifecycle Methods: these methods are called automatically by the framework
  	  as the bricks are created, attached, detached, and destroyed respectively.
	*/	
	public void init();	
	public void begin();
	public void end();
	public void destroy();

	/*
  	  Implementation primitives required by the architecture
	*/
  
    //To be imported: UserPreferences
    public UserPreferences getUserPreferences ()  ;        
        
}