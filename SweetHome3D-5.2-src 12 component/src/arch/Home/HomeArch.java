package arch.Home;


import arch.interfaces.HomeInterface;

import com.eteks.sweethome3d.model.Home;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class HomeArch extends AbstractMyxSimpleBrick implements HomeInterface
{
    public static final IMyxName msg_HomeInterface = MyxUtils.createName("arch.interfaces.HomeInterface");


	private IHomeImp _imp;

    public HomeArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IHomeImp getImplementation(){
        try{
			return new HomeImp();    
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
		if (arg0.equals(msg_HomeInterface)){
			return this;
		}        
		return null;
	}
  
    //To be imported: Home
    public Home GetHome ()   {
		return _imp.GetHome();
    }    
}