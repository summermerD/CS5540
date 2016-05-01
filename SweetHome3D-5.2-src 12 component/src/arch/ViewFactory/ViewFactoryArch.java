package arch.ViewFactory;


import arch.interfaces.GetViewFactoryInt;

import com.eteks.sweethome3d.swing.SwingViewFactory;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class ViewFactoryArch extends AbstractMyxSimpleBrick implements GetViewFactoryInt
{
    public static final IMyxName msg_GetViewFactoryInt = MyxUtils.createName("arch.interfaces.GetViewFactoryInt");


	private IViewFactoryImp _imp;

    public ViewFactoryArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IViewFactoryImp getImplementation(){
        try{
			return new ViewFactoryImp();    
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
		if (arg0.equals(msg_GetViewFactoryInt)){
			return this;
		}        
		return null;
	}
  
    //To be imported: SwingViewFactory
    public SwingViewFactory GetSwingViewFactory ()   {
		return _imp.GetSwingViewFactory();
    }    
}