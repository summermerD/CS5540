package arch.Start;


import arch.SweetHome3DMain.HomeFrameController;
import com.eteks.sweethome3d.model.Home;

import arch.Start.StartArch;

public interface IStartImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (StartArch arch);
	public StartArch getArch();
	
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
  
    
    public HomeFrameController GetStart (Home home)  ;        
}