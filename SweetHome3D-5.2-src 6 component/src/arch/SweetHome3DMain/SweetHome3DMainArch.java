package arch.SweetHome3DMain;


import arch.interfaces.MainToBuildHome;
import arch.interfaces.MainToGetUserPreferences;
import arch.interfaces.MainToInitialization;
import arch.interfaces.MainToJava3DErrorListener;
import arch.interfaces.MainToMacConfig;
import arch.interfaces.MainToSwingEnsurance;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class SweetHome3DMainArch extends AbstractMyxSimpleBrick
{
    public static final IMyxName msg_MainToInitialization = MyxUtils.createName("arch.interfaces.MainToInitialization");
    public static final IMyxName msg_MainToMacConfig = MyxUtils.createName("arch.interfaces.MainToMacConfig");
    public static final IMyxName msg_MainToJava3DErrorListener = MyxUtils.createName("arch.interfaces.MainToJava3DErrorListener");
    public static final IMyxName msg_MainToBuildHome = MyxUtils.createName("arch.interfaces.MainToBuildHome");
    public static final IMyxName msg_MainToGetUserPreferences = MyxUtils.createName("arch.interfaces.MainToGetUserPreferences");
    public static final IMyxName msg_MainToSwingEnsurance = MyxUtils.createName("arch.interfaces.MainToSwingEnsurance");

    public MainToInitialization OUT_MainToInitialization;
    public MainToMacConfig OUT_MainToMacConfig;
    public MainToJava3DErrorListener OUT_MainToJava3DErrorListener;
    public MainToBuildHome OUT_MainToBuildHome;
    public MainToGetUserPreferences OUT_MainToGetUserPreferences;
    public MainToSwingEnsurance OUT_MainToSwingEnsurance;

	private ISweetHome3DMainImp _imp;

    public SweetHome3DMainArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected ISweetHome3DMainImp getImplementation(){
        try{
			return new SweetHome3DMainImp();    
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void init(){
        _imp.init();
    }
    
    public void begin(){
        OUT_MainToInitialization = (MainToInitialization) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToInitialization);
        if (OUT_MainToInitialization == null){
 			System.err.println("Error: Interface arch.interfaces.MainToInitialization returned null");
			return;       
        }
        OUT_MainToMacConfig = (MainToMacConfig) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToMacConfig);
        if (OUT_MainToMacConfig == null){
 			System.err.println("Error: Interface arch.interfaces.MainToMacConfig returned null");
			return;       
        }
        OUT_MainToJava3DErrorListener = (MainToJava3DErrorListener) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToJava3DErrorListener);
        if (OUT_MainToJava3DErrorListener == null){
 			System.err.println("Error: Interface arch.interfaces.MainToJava3DErrorListener returned null");
			return;       
        }
        OUT_MainToBuildHome = (MainToBuildHome) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToBuildHome);
        if (OUT_MainToBuildHome == null){
 			System.err.println("Error: Interface arch.interfaces.MainToBuildHome returned null");
			return;       
        }
        OUT_MainToGetUserPreferences = (MainToGetUserPreferences) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToGetUserPreferences);
        if (OUT_MainToGetUserPreferences == null){
 			System.err.println("Error: Interface arch.interfaces.MainToGetUserPreferences returned null");
			return;       
        }
        OUT_MainToSwingEnsurance = (MainToSwingEnsurance) MyxUtils.getFirstRequiredServiceObject(this,msg_MainToSwingEnsurance);
        if (OUT_MainToSwingEnsurance == null){
 			System.err.println("Error: Interface arch.interfaces.MainToSwingEnsurance returned null");
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
		return null;
	}
}