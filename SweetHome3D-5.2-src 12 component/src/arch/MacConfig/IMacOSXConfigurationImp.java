package arch.MacConfig;


import arch.MacConfig.MacConfigArch;

import arch.SweetHome3DMain.SweetHome3DMainImp;

import javax.swing.JFrame;

public interface IMacOSXConfigurationImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (MacConfigArch arch);
	public MacConfigArch getArch();
	
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
  
    //To be imported: JFrame,SweetHome3DMainImp
    public void bindToApplicationMenu (final SweetHome3DMainImp homeApplication)  ;        
    public boolean isWindowFullScreen (final JFrame frame)  ;        
       
}