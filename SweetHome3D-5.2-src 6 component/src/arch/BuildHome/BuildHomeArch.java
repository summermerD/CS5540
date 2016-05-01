package arch.BuildHome;


import arch.interfaces.BuildHomeToMacConfig;
import arch.interfaces.MainToBuildHome;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class BuildHomeArch extends AbstractMyxSimpleBrick implements MainToBuildHome
{
    public static final IMyxName msg_MainToBuildHome = MyxUtils.createName("arch.interfaces.MainToBuildHome");
    public static final IMyxName msg_BuildHomeToMacConfig = MyxUtils.createName("arch.interfaces.BuildHomeToMacConfig");

    public BuildHomeToMacConfig OUT_BuildHomeToMacConfig;

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