package arch.SwingEnsurance;

import javax.swing.UIManager;

import com.eteks.sweethome3d.swing.SwingTools;
import com.eteks.sweethome3d.tools.OperatingSystem;

public class SwingEnsuranceImp implements ISwingEnsuranceImp
{
	private SwingEnsuranceArch _arch;

    public SwingEnsuranceImp (){
    }

	public void setArch(SwingEnsuranceArch arch){
		_arch = arch;
	}
	public SwingEnsuranceArch getArch(){
		return _arch;
	}

	/*
  	  Myx Lifecycle Methods: these methods are called automatically by the framework
  	  as the bricks are created, attached, detached, and destroyed respectively.
	*/	
	public void init(){
	    //TODO Auto-generated method stub
	}
	public void begin(){
		//TODO Auto-generated method stub
	}
	public void end(){
		//TODO Auto-generated method stub
	}
	public void destroy(){
		//TODO Auto-generated method stub
	}

	/*
  	  Implementation primitives required by the architecture
	*/
  
    
    public void initLookAndFeel ()   {
		//TODO Auto-generated method stub
      try {
        // Apply current system look and feel if swing.defaultlaf isn't defined
        UIManager.setLookAndFeel(System.getProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName()));
        // Change default titled borders under Mac OS X 10.5
        if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
          UIManager.put("TitledBorder.border", UIManager.getBorder("TitledBorder.aquaVariant"));
        }
        SwingTools.updateSwingResourceLanguage(_arch.OUT_SwingEnsuranceToGetUserPreferences.getUserPreferences());
      } catch (Exception ex) {
        // Too bad keep current look and feel
      }	
    }
}