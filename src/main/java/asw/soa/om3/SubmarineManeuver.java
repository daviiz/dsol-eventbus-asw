package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om2.Environment;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

public class SubmarineManeuver extends EventProducer {

    private ModelData _mdata = null;
    private EntityMSG lastThreat = null;

    public ModelData get_mdata() {
        return _mdata;
    }

    public void set_mdata(ModelData _mdata) {
        this._mdata = _mdata;
    }

    public EntityMSG getLastThreat() {
        return lastThreat;
    }

    public void setLastThreat(EntityMSG lastThreat) {
        this.lastThreat = lastThreat;
    }

    public static final EventType SUBMARINE_LOCATION_MSG = new EventType("SUBMARINE_LOCATION_MSG");

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public SubmarineManeuver(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }

    public synchronized void update(final ModelData _mdata, final EntityMSG lastThreat) throws SimRuntimeException {
        this._mdata = _mdata;
        this.lastThreat = lastThreat;
    }
    public synchronized void next() throws SimRuntimeException {
        _mdata.origin = _mdata.destination;
        // this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 +
        // stream.nextInt(0, 200), 0);
        _mdata.destination = new CartesianPoint(_mdata.destination.x + _mdata.speed, _mdata.destination.y + _mdata.speed, 0);
        _mdata.startTime = this.simulator.getSimulatorTime();
        _mdata.stopTime = _mdata.startTime +  SimUtil.interval;

        //this.simulator.scheduleEventRel(SimUtil.interval, this, Environment.getInstance(), "msgCast", new Object[]{new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y)});
        this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", null);
        super.fireTimedEvent(SUBMARINE_LOCATION_MSG,
                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
                this.simulator.getSimTime());
    }
    public void startSim()throws SimRuntimeException{
        this.next();
    }
}
