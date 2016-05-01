package arch.HomeController;


import com.eteks.sweethome3d.viewcontroller.HomeController;

import arch.interfaces.HomeControllerToBuildHome;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class HomeControllerArch extends AbstractMyxSimpleBrick implements HomeControllerToBuildHome
{
    public static final IMyxName msg_HomeControllerToBuildHome = MyxUtils.createName("arch.interfaces.HomeControllerToBuildHome");


	private IHomeControllerImp _imp;

    public HomeControllerArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IHomeControllerImp getImplementation(){
        try{
			return new HomeControllerImp();    
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void init(){
        _imp.init();
    }
    
    public void begin(){
        _imp.begin();
    }
    
    public void end(){
        _imp.end();
    }
    
    public void destroy(){
        _imp.destroy();
    }
    
	public Object getServiceObject(IMyxName arg0) {
		if (arg0.equals(msg_HomeControllerToBuildHome)){
			return this;
		}        
		return null;
	}
  
        public HomeController GetHomeController(){
          return _imp.GetHomeController();
        }
}