package arch.SweetHome3DMain;


import arch.SweetHome3DMain.SweetHome3DMainArch;

public interface ISweetHome3DMainImp 
{

	/*
	  Getter and Setter of architecture reference
	*/
    public void setArch (SweetHome3DMainArch arch);
	public SweetHome3DMainArch getArch();
	
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
}