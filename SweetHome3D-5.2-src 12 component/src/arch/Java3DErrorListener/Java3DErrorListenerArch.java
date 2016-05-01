package arch.Java3DErrorListener;


import arch.interfaces.MainToJava3DErrorListener;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class Java3DErrorListenerArch extends AbstractMyxSimpleBrick implements MainToJava3DErrorListener
{
    public static final IMyxName msg_MainToJava3DErrorListener = MyxUtils.createName("arch.interfaces.MainToJava3DErrorListener");


	private IJava3DErrorListenerImp _imp;

    public Java3DErrorListenerArch (){
		_imp = getImplementation();
		if (_imp != null){
			_imp.setArch(this);
		} else {
			System.exit(1);
		}
	}
    
    protected IJava3DErrorListenerImp getImplementation(){
        try{
			return new Java3DErrorListenerImp();    
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
		if (arg0.equals(msg_MainToJava3DErrorListener)){
			return this;
		}        
		return null;
	}
  
    
    public void addComponent3DRenderingErrorObserver ()   {
		_imp.addComponent3DRenderingErrorObserver();
    }    
}