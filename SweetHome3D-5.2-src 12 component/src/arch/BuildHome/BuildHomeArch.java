package arch.BuildHome;


import arch.interfaces.BuildHomeToMacConfig;
import arch.interfaces.GetViewFactoryInt;
import arch.interfaces.HomeControllerToBuildHome;
import arch.interfaces.HomeInterface;
import arch.interfaces.HomeRecorderInterface;
import arch.interfaces.MainToBuildHome;
import arch.interfaces.StartToBuildHome;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class BuildHomeArch extends AbstractMyxSimpleBrick implements MainToBuildHome
{
    public static final IMyxName msg_MainToBuildHome = MyxUtils.createName("arch.interfaces.MainToBuildHome");
    public static final IMyxName msg_BuildHomeToMacConfig = MyxUtils.createName("arch.interfaces.BuildHomeToMacConfig");
    public static final IMyxName msg_StartToBuildHome = MyxUtils.createName("arch.interfaces.StartToBuildHome");
    public static final IMyxName msg_GetViewFactoryInt = MyxUtils.createName("arch.interfaces.GetViewFactoryInt");
    public static final IMyxName msg_HomeInterface = MyxUtils.createName("arch.interfaces.HomeInterface");
    public static final IMyxName msg_HomeRecorderInterface = MyxUtils.createName("arch.interfaces.HomeRecorderInterface");
    public static final IMyxName msg_HomeControllerToBuildHome = MyxUtils.createName("arch.interfaces.HomeControllerToBuildHome");

    public BuildHomeToMacConfig OUT_BuildHomeToMacConfig;
    public StartToBuildHome OUT_StartToBuildHome;
    public GetViewFactoryInt OUT_GetViewFactoryInt;
    public HomeInterface OUT_HomeInterface;
    public HomeRecorderInterface OUT_HomeRecorderInterface;
    public HomeControllerToBuildHome OUT_HomeControllerToBuildHome;

	private IBuildHomeImp _imp;

    public BuildHomeArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IBuildHomeImp getImplementation(){
        try{
			return new BuildHomeImp();    
        } catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void init(){
        _imp.init();
    }
    
    public void begin(){
        OUT_BuildHomeToMacConfig = (BuildHomeToMacConfig) MyxUtils.getFirstRequiredServiceObject(this,msg_BuildHomeToMacConfig);
        if (OUT_BuildHomeToMacConfig == null){
 			System.err.println("Error: Interface arch.interfaces.BuildHomeToMacConfig returned null");
			return;       
        }
        OUT_StartToBuildHome = (StartToBuildHome) MyxUtils.getFirstRequiredServiceObject(this,msg_StartToBuildHome);
        if (OUT_StartToBuildHome == null){
 			System.err.println("Error: Interface arch.interfaces.StartToBuildHome returned null");
			return;       
        }
        OUT_GetViewFactoryInt = (GetViewFactoryInt) MyxUtils.getFirstRequiredServiceObject(this,msg_GetViewFactoryInt);
        if (OUT_GetViewFactoryInt == null){
 			System.err.println("Error: Interface arch.interfaces.GetViewFactoryInt returned null");
			return;       
        }
        OUT_HomeInterface = (HomeInterface) MyxUtils.getFirstRequiredServiceObject(this,msg_HomeInterface);
        if (OUT_HomeInterface == null){
 			System.err.println("Error: Interface arch.interfaces.HomeInterface returned null");
			return;       
        }
        OUT_HomeRecorderInterface = (HomeRecorderInterface) MyxUtils.getFirstRequiredServiceObject(this,msg_HomeRecorderInterface);
        if (OUT_HomeRecorderInterface == null){
 			System.err.println("Error: Interface arch.interfaces.HomeRecorderInterface returned null");
			return;       
        }
        OUT_HomeControllerToBuildHome = (HomeControllerToBuildHome) MyxUtils.getFirstRequiredServiceObject(this,msg_HomeControllerToBuildHome);
        if (OUT_HomeControllerToBuildHome == null){
 			System.err.println("Error: Interface arch.interfaces.HomeControllerToBuildHome returned null");
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
		if (arg0.equals(msg_MainToBuildHome)){
			return this;
		}        
		return null;
	}
  
    
    public void buildHome ()   {
		_imp.buildHome();
    }    
}