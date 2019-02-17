package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om2.Environment;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;

import javax.naming.NamingException;
import java.rmi.RemoteException;
/**
 * 雷达探测接受消息，发送给Controller
 * */
public class DecoySensor implements EventListenerInterface {

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private ModelData _mdata = null;

    private EntityMSG lastThreat = null;

    private DecoyController controller = null;

    public DecoySensor(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
        Environment.getInstance().addListener(this,Environment.ENVIRONMENT_SONAR_DETECTED);
    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        if(_mdata!=null && _mdata.status){
            EntityMSG tmp = (EntityMSG) event.getContent();
            if (tmp.belong != this._mdata.belong) {

                // System.out.println(name+" received msg: "+tmp.name+" current
                // location:x="+tmp.x+", y="+tmp.y);
                double dis = SimUtil.calcLength(this._mdata.origin.x, this._mdata.origin.y, tmp.x, tmp.y);
                if (dis < this._mdata.detectRange) {
                    lastThreat = tmp;
                    //雷达探测接受消息，发送给Controller
                    try {
                        if(controller!= null)
                            this.simulator.scheduleEventRel(2.0,this, controller, "decide", new Object[]{ _mdata,tmp });
                    } catch (SimRuntimeException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public synchronized void fire(final EntityMSG object) throws RemoteException, NamingException, SimRuntimeException {
        //isFired = true;
        lastThreat = object;
        // 视图组件注册：
        Visual2dService.getInstance().register(this._mdata.name, simulator, this._mdata);
        if(controller!= null)
            this.simulator.scheduleEventRel(2.0,this, controller, "decide", new Object[]{ _mdata,lastThreat });
    }

    public DecoyController getController() {
        return controller;
    }

    public void setController(DecoyController controller) {
        this.controller = controller;
    }

    public ModelData get_mdata() {
        return _mdata;
    }

    public void set_mdata(ModelData _mdata) {
        this._mdata = _mdata;
    }
}
