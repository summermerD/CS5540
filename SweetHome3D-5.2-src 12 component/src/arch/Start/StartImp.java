package arch.Start;

import java.util.Map;

import arch.SweetHome3DMain.HomeFrameController;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;



public class StartImp implements IStartImp
{
        private Map<Home, HomeFrameController> homeFrameControllers;
	private StartArch _arch;
	

    public StartImp (){
    }

	public void setArch(StartArch arch){
		_arch = arch;
	}
	public StartArch getArch(){
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
  

    public HomeFrameController GetStart (Home home)   {
      return this.homeFrameControllers.get(home);

    }
}