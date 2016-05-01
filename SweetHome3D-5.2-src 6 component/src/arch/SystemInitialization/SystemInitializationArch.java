package arch.SystemInitialization;


import arch.interfaces.MainToInitialization;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class SystemInitializationArch extends AbstractMyxSimpleBrick implements MainToInitialization
{
    public static final IMyxName msg_MainToInitialization = MyxUtils.createName("arch.interfaces.MainToInitialization");


	private ISystemInitializationImp _imp;

    public SystemInitializationArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected ISystemInitializationImp getImplementation(){
        try{
			return new SystemInitializationImp();    
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
		if (arg0.equals(msg_MainToInitialization)){
			return this;
		}        
		return null;
	}
  

    public void initialization(String [] args) {
      _imp.initialization(args);      
    }    
}