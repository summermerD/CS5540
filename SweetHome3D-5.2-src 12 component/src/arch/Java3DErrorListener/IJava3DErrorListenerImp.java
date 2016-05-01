package arch.Java3DErrorListener;


import arch.Java3DErrorListener.Java3DErrorListenerArch;

public interface IJava3DErrorListenerImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (Java3DErrorListenerArch arch);
	public Java3DErrorListenerArch getArch();
	
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
  
    
    public void addComponent3DRenderingErrorObserver ()  ;        
}