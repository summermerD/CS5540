package arch.HomeController;

import arch.BuildHome.BuildHomeImp;
import arch.HomeController.IHomeControllerImp;
import com.eteks.sweethome3d.viewcontroller.HomeController;

public class HomeControllerImp implements IHomeControllerImp
{
	private HomeControllerArch _arch;
	private BuildHomeImp bhi = new BuildHomeImp();

    public HomeControllerImp (){
    }

	public void setArch(HomeControllerArch arch){
		_arch = arch;
	}
	public HomeControllerArch getArch(){
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
  
        public HomeController GetHomeController(){
           return bhi.createHomeFrameController(bhi.createHome()).getHomeController();
        }
}