package arch.ViewFactory;


import arch.ViewFactory.ViewFactoryArch;

import com.eteks.sweethome3d.swing.SwingViewFactory;

public interface IViewFactoryImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (ViewFactoryArch arch);
	public ViewFactoryArch getArch();
	
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
  
    //To be imported: SwingViewFactory
    public SwingViewFactory GetSwingViewFactory ()  ;        
}