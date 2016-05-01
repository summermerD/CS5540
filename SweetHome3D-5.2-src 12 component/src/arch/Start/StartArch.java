package arch.Start;


import arch.interfaces.StartToBuildHome;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class StartArch extends AbstractMyxSimpleBrick implements StartToBuildHome
{
    public static final IMyxName msg_StartToBuildHome = MyxUtils.createName("arch.interfaces.StartToBuildHome");


	private IStartImp _imp;

    public StartArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IStartImp getImplementation(){
        try{
			return new StartImp();    
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
		if (arg0.equals(msg_StartToBuildHome)){
			return this;
		}        
		return null;
	}
  
    
    public void GetStart (final String [] args)   {
		_imp.GetStart(args);
    }    
}