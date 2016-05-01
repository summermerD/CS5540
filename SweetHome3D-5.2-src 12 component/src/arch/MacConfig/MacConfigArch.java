package arch.MacConfig;


import arch.SweetHome3DMain.SweetHome3DMainImp;

import arch.interfaces.BuildHomeToMacConfig;
import arch.interfaces.MainToMacConfig;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

import javax.swing.JFrame;

public class MacConfigArch extends AbstractMyxSimpleBrick implements MainToMacConfig,BuildHomeToMacConfig
{
    public static final IMyxName msg_MainToMacConfig = MyxUtils.createName("arch.interfaces.MainToMacConfig");
    public static final IMyxName msg_BuildHomeToMacConfig = MyxUtils.createName("arch.interfaces.BuildHomeToMacConfig");


	private IMacOSXConfigurationImp _imp;

    public MacConfigArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IMacOSXConfigurationImp getImplementation(){
        try{
			return new MacConfigImp();    
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
		if (arg0.equals(msg_MainToMacConfig)){
			return this;
		}        
		if (arg0.equals(msg_BuildHomeToMacConfig)){
			return this;
		}        
		return null;
	}
  
    //To be imported: JFrame,SweetHome3DMainImp
    public void bindToApplicationMenu (final SweetHome3DMainImp homeApplication)   {
		_imp.bindToApplicationMenu(homeApplication);
    }    
    public boolean isWindowFullScreen (final JFrame frame)   {
		return _imp.isWindowFullScreen(frame);
    }    
  
 
}