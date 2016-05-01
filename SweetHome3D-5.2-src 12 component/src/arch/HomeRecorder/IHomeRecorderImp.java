package arch.HomeRecorder;


import arch.HomeRecorder.HomeRecorderArch;

import com.eteks.sweethome3d.io.HomeFileRecorder;

public interface IHomeRecorderImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (HomeRecorderArch arch);
	public HomeRecorderArch getArch();
	
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
  
    //To be imported: HomeRecorder
    public HomeFileRecorder GetHomeRecorder ()  ;        
}