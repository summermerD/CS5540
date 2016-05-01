package arch.HomeController;


import com.eteks.sweethome3d.viewcontroller.HomeController;

import arch.HomeController.HomeControllerArch;

public interface IHomeControllerImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (HomeControllerArch arch);
	public HomeControllerArch getArch();
	
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
  
        public HomeController GetHomeController();
}