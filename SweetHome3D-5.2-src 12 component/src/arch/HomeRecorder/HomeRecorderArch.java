package arch.HomeRecorder;


import arch.interfaces.HomeRecorderInterface;

import com.eteks.sweethome3d.io.HomeFileRecorder;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class HomeRecorderArch extends AbstractMyxSimpleBrick implements HomeRecorderInterface
{
    public static final IMyxName msg_HomeRecorderInterface = MyxUtils.createName("arch.interfaces.HomeRecorderInterface");


	private IHomeRecorderImp _imp;

    public HomeRecorderArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IHomeRecorderImp getImplementation(){
        try{
			return new HomeRecorderImp();    
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
		if (arg0.equals(msg_HomeRecorderInterface)){
			return this;
		}        
		return null;
	}
  
    //To be imported: HomeRecorder
    public HomeFileRecorder GetHomeRecorder ()   {
		return _imp.GetHomeRecorder();
    }    
}