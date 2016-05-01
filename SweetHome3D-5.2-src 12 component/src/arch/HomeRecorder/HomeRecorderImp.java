package arch.HomeRecorder;

import arch.BuildHome.BuildHomeImp;
import com.eteks.sweethome3d.io.HomeFileRecorder;

public class HomeRecorderImp implements IHomeRecorderImp
{
        private BuildHomeImp bhi = new BuildHomeImp();
	private HomeRecorderArch _arch;

    public HomeRecorderImp (){
    }

	public void setArch(HomeRecorderArch arch){
		_arch = arch;
	}
	public HomeRecorderArch getArch(){
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
  
    //To be imported: HomeRecorder
    public HomeFileRecorder GetHomeRecorder ()   {
		//TODO Auto-generated method stub
		return new HomeFileRecorder(0, false, bhi.getUserPreferences(), false);
    }
}