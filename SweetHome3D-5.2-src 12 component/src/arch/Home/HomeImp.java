package arch.Home;

import arch.BuildHome.BuildHomeArch;
import arch.BuildHome.BuildHomeImp;
import com.eteks.sweethome3d.model.Home;

public class HomeImp implements IHomeImp
{
	private HomeArch _arch;

    public HomeImp (){
    }
        BuildHomeImp bhi = new BuildHomeImp();

	public void setArch(HomeArch arch){
		_arch = arch;
	}
	public HomeArch getArch(){
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
  
    //To be imported: Home
    public Home GetHome ()   {
		//TODO Auto-generated method stub
        return new Home(bhi.getUserPreferences().getNewWallHeight());
    }
}