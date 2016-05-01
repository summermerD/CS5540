package arch.SwingEnsurance;


import arch.interfaces.MainToSwingEnsurance;
import arch.interfaces.SwingEnsuranceToGetUserPreferences;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class SwingEnsuranceArch extends AbstractMyxSimpleBrick implements MainToSwingEnsurance
{
    public static final IMyxName msg_MainToSwingEnsurance = MyxUtils.createName("arch.interfaces.MainToSwingEnsurance");
    public static final IMyxName msg_SwingEnsuranceToGetUserPreferences = MyxUtils.createName("arch.interfaces.SwingEnsuranceToGetUserPreferences");

    public SwingEnsuranceToGetUserPreferences OUT_SwingEnsuranceToGetUserPreferences;

	private ISwingEnsuranceImp _imp;

    public SwingEnsuranceArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected ISwingEnsuranceImp getImplementation(){
        try{
			return new SwingEnsuranceImp();    
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void init(){
        _imp.init();
    }
    
    public void begin(){
        OUT_SwingEnsuranceToGetUserPreferences = (SwingEnsuranceToGetUserPreferences) MyxUtils.getFirstRequiredServiceObject(this,msg_SwingEnsuranceToGetUserPreferences);
        if (OUT_SwingEnsuranceToGetUserPreferences == null){
 			System.err.println("Error: Interface arch.interfaces.SwingEnsuranceToGetUserPreferences returned null");
			return;       
        }
        _imp.begin();
    }
    
    public void end(){
        _imp.end();
    }
    
    public void destroy(){
        _imp.destroy();
    }
    
	public Object getServiceObject(IMyxName arg0) {
		if (arg0.equals(msg_MainToSwingEnsurance)){
			return this;
		}        
		return null;
	}
  
    
    public void initLookAndFeel ()   {
		_imp.initLookAndFeel();
    }    
}