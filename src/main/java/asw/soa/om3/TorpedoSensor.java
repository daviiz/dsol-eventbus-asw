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

public class TorpedoSensor implements EventListenerInterface {

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private ModelData _mdata = null;

    private EntityMSG lastTarget = null;

    private TorpedoController controller = null;

    private double lastDistance = 250.0;

    public TorpedoSensor(final DEVSSimulatorInterface.TimeDouble simulator) {
        this.simulator = simulator;
        Environment.getInstance().addListener(this, Environment.ENVIRONMENT_SONAR_DETECTED);
    }

    @Override
    public void notify(EventInterface event) {
        if (_mdata != null && _mdata.status) {
            EntityMSG threatTarget = null;
            EntityMSG tmp = (EntityMSG) event.getContent();
            if (tmp.name.equals(_mdata.name))
                return;
            if (tmp.belong != this._mdata.belong) {
                //EntityMSG tmp = (EntityMSG) event.getContent();
                double tmpL = SimUtil.calcLength(this._mdata.origin.x, this._mdata.origin.y, tmp.x, tmp.y);
                if (tmpL < _mdata.detectRange) {
                    threatTarget = tmp;
                }
            }
            //雷达探测接受消息，发送给Controller
            try {
                if (controller != null)
                    this.simulator.scheduleEventRel(2.0, this, controller, "decide", new Object[]{_mdata, threatTarget});
            } catch (SimRuntimeException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void fire(final ModelData data, final EntityMSG object) throws RemoteException, NamingException, SimRuntimeException {
        //isFired = true;
        lastTarget = new EntityMSG("0");
        if (!object.name.equals("0")) {
            lastTarget = object;
        }
        this.set_mdata(data);
        // 视图组件注册：
        Visual2dService.getInstance().register(this._mdata.name, simulator, this._mdata);
        if (controller != null)
            this.simulator.scheduleEventRel(2.0, this, controller, "decide", new Object[]{_mdata, lastTarget});
    }

    public ModelData get_mdata() {
        return _mdata;
    }

    public void set_mdata(ModelData _mdata) {
        this._mdata = _mdata;
    }

    public TorpedoController getController() {
        return controller;
    }

    public void setController(TorpedoController controller) {
        this.controller = controller;
    }
}
