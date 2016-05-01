package arch.GetUserPreferences;


import arch.interfaces.MainToGetUserPreferences;
import arch.interfaces.SwingEnsuranceToGetUserPreferences;

import com.eteks.sweethome3d.model.UserPreferences;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class GetUserPreferencesArch extends AbstractMyxSimpleBrick implements MainToGetUserPreferences,SwingEnsuranceToGetUserPreferences
{
    public static final IMyxName msg_MainToGetUserPreferences = MyxUtils.createName("arch.interfaces.MainToGetUserPreferences");
    public static final IMyxName msg_SwingEnsuranceToGetUserPreferences = MyxUtils.createName("arch.interfaces.SwingEnsuranceToGetUserPreferences");


	private IGetUserPreferencesImp _imp;

    public GetUserPreferencesArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IGetUserPreferencesImp getImplementation(){
        try{
			return new GetUserPreferencesImp();    
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
		if (arg0.equals(msg_MainToGetUserPreferences)){
			return this;
		}        
		if (arg0.equals(msg_SwingEnsuranceToGetUserPreferences)){
			return this;
		}        
		return null;
	}
  
    //To be imported: UserPreferences
    public UserPreferences getUserPreferences ()   {
		return _imp.getUserPreferences();
    }    
  
  
}