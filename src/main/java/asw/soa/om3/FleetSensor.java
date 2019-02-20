package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om2.Environment;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;

import java.rmi.RemoteException;

public class FleetSensor implements EventListenerInterface {

    private ModelData _mdata = null;

    private FleetController controller = null;

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public FleetSensor(final DEVSSimulatorInterface.TimeDouble simulator) {
        this.simulator = simulator;
        Environment.getInstance().addListener(this, Environment.ENVIRONMENT_SONAR_DETECTED);
    }

    public FleetSensor(final DEVSSimulatorInterface.TimeDouble simulator, final FleetController controller) {
        this.simulator = simulator;
        Environment.getInstance().addListener(this, Environment.ENVIRONMENT_SONAR_DETECTED);

    }

    public void startSim() {
        try {
            this.simulator.scheduleEventNow(this, controller, "decide", new Object[]{_mdata, new EntityMSG("0")});
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        if (_mdata != null && _mdata.status) {
            EntityMSG lastThreat = new EntityMSG("0");
            EntityMSG tmp = (EntityMSG) event.getContent();
            if (tmp.name.equals(_mdata.name))
                return;
            if (tmp.belong == this._mdata.belong) {

            } else if (tmp.belong != this._mdata.belong) {
                double dis = SimUtil.calcLength(this._mdata.origin.x, this._mdata.origin.y, tmp.x, tmp.y);

                if (dis < _mdata.detectRange) {
                    lastThreat = tmp;

                }
            }
            //雷达探测接受消息，发送给Controller
            try {
                if (controller != null)
                    this.simulator.scheduleEventRel(2.0, this, controller, "decide", new Object[]{_mdata, lastThreat});
            } catch (SimRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public ModelData get_mdata() {
        return _mdata;
    }

    public void set_mdata(ModelData _mdata) {
        this._mdata = _mdata;
    }

    public FleetController getController() {
        return controller;
    }

    public void setController(FleetController controller) {
        this.controller = controller;
    }
}
