package arch.SwingEnsurance;


import arch.SwingEnsurance.SwingEnsuranceArch;

public interface ISwingEnsuranceImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (SwingEnsuranceArch arch);
	public SwingEnsuranceArch getArch();
	
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
  
    
    public void initLookAndFeel ()  ;        
}