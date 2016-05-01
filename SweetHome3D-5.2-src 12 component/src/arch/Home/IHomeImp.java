package arch.Home;


import arch.Home.HomeArch;

import com.eteks.sweethome3d.model.Home;

public interface IHomeImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (HomeArch arch);
	public HomeArch getArch();
	
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
  
    //To be imported: Home
    public Home GetHome ()  ;        
}