package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

public class FleetManeuver  extends EventProducer {
    private static final EventType FLEET_LOCATION_MSG = new EventType("FLEET_LOCATION_MSG");

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public FleetManeuver(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }

    private synchronized void next(final ModelData _mdata, final EntityMSG lastThreat) throws SimRuntimeException {
        _mdata.origin = _mdata.destination;

        if (!_mdata.status) {
            _mdata.destination = new CartesianPoint(_mdata.destination.x, _mdata.destination.y, 0);
        } else if (lastThreat == null) {
            _mdata.destination = new CartesianPoint(_mdata.destination.x + _mdata.speed, _mdata.destination.y + _mdata.speed,
                    0);
        } else {
            _mdata.destination = SimUtil.nextPoint(_mdata.origin.x, _mdata.origin.y, lastThreat.x,
                    lastThreat.y, _mdata.speed, false);
        }

        _mdata.startTime = this.simulator.getSimulatorTime();
        _mdata.stopTime = _mdata.startTime + 7;

        // System.out.println(Math.abs(new DistNormal(stream, 9, 1.8).draw()));

        this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", null);
        super.fireTimedEvent(FLEET_LOCATION_MSG,
                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
                this.simulator.getSimTime());
    }
}
