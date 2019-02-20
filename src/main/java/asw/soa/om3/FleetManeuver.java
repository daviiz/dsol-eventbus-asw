package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

public class FleetManeuver extends EventProducer {
    public static final EventType FLEET_LOCATION_MSG = new EventType("FLEET_LOCATION_MSG");

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private ModelData _mdata = null;

    private EntityMSG lastThreat = null;

    public FleetManeuver(final DEVSSimulatorInterface.TimeDouble simulator) {
        this.simulator = simulator;
    }

    public synchronized void update(final ModelData _mdata, final EntityMSG lastThreat) throws SimRuntimeException {
//        _mdata.origin = _mdata.destination;

//        if (!_mdata.status) {
//            _mdata.destination = new CartesianPoint(_mdata.destination.x, _mdata.destination.y, 0);
//        } else if (lastThreat.name.equals("0")) {
//            _mdata.destination = new CartesianPoint(_mdata.destination.x + _mdata.speed, _mdata.destination.y + _mdata.speed,
//                    0);
//        } else {
//            _mdata.destination = SimUtil.nextPoint(_mdata.origin.x, _mdata.origin.y, lastThreat.x,
//                    lastThreat.y, _mdata.speed, false);
//        }

        this._mdata = _mdata;
        this.lastThreat = lastThreat;
        //_mdata.startTime = this.simulator.getSimulatorTime();
        //_mdata.stopTime = _mdata.startTime + SimUtil.interval;
        //this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", new Object[]{ _mdata ,new EntityMSG("0")});
        //this.simulator.scheduleEventRel(12.0, this, Environment.getInstance(), "msgCast", new Object[]{new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y)});
//        super.fireTimedEvent(FLEET_LOCATION_MSG,
//                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
//                this.simulator.getSimTime());

    }

    public synchronized void next() throws SimRuntimeException {
        _mdata.origin = _mdata.destination;

        if (!_mdata.status) {
            _mdata.destination = new CartesianPoint(_mdata.destination.x, _mdata.destination.y, 0);
        } else if (lastThreat.name.equals("0")) {
            _mdata.destination = new CartesianPoint(_mdata.destination.x + _mdata.speed, _mdata.destination.y + _mdata.speed,
                    0);
        } else {
            _mdata.destination = SimUtil.nextPoint(_mdata.origin.x, _mdata.origin.y, lastThreat.x,
                    lastThreat.y, _mdata.speed, false);
        }

        _mdata.startTime = this.simulator.getSimulatorTime();
        _mdata.stopTime = _mdata.startTime + SimUtil.interval;
        this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", null);
        //this.simulator.scheduleEventRel(12.0, this, Environment.getInstance(), "msgCast", new Object[]{new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y)});
        super.fireTimedEvent(FLEET_LOCATION_MSG,
                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
                this.simulator.getSimTime());

    }

    public void startSim()throws SimRuntimeException{
            this.next();
    }

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
}
