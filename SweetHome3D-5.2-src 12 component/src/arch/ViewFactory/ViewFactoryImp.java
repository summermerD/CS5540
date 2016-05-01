package arch.ViewFactory;


import com.eteks.sweethome3d.swing.SwingViewFactory;

public class ViewFactoryImp implements IViewFactoryImp
{
	private ViewFactoryArch _arch;

    public ViewFactoryImp (){
    }

	public void setArch(ViewFactoryArch arch){
		_arch = arch;
	}
	public ViewFactoryArch getArch(){
		return _arch;
	}

	/*
  	  Myx Lifecycle Methods: these methods are called automatically by the framework
  	  as the bricks are created, attached, detached, and destroyed respectively.
	*/	
	public void init(){
	    //TODO Auto-generated method stub
	}
	public void begin(){
		//TODO Auto-generated method stub
	}
	public void end(){
		//TODO Auto-generated method stub
	}
	public void destroy(){
		//TODO Auto-generated method stub
	}

	/*
  	  Implementation primitives required by the architecture
	*/
  
    //To be imported: SwingViewFactory
    public SwingViewFactory GetSwingViewFactory ()   {
		//TODO Auto-generated method stub
		return new SwingViewFactory();
    }
}