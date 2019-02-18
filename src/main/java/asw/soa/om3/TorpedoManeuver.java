package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om2.Environment;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;

public class TorpedoManeuver extends EventProducer {

    public static final EventType TORPEDO_LOCATION_MSG = new EventType("TORPEDO_LOCATION_MSG");

    //private boolean isFired = false;

    /** the simulator. */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public TorpedoManeuver(final DEVSSimulatorInterface.TimeDouble simulator )
    {
        this.simulator = simulator;
    }

    public synchronized void next(final ModelData _mdata, final EntityMSG lastTarget) throws SimRuntimeException{
        _mdata.origin = _mdata.destination;

        if (lastTarget.name.equals("0") || lastTarget.status == false) {

        } else {
            _mdata.destination = SimUtil.nextPoint(_mdata.origin.x, _mdata.origin.y, lastTarget.x,
                    lastTarget.y, _mdata.speed, true);
        }
        _mdata.startTime = this.simulator.getSimulatorTime();

        _mdata.stopTime = _mdata.startTime  + SimUtil.interval;

        //this.simulator.scheduleEventAbs(_mdata.stopTime, this, Environment.getInstance(), "msgCast", new Object[]{new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y)});
        this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", new Object[]{ _mdata ,new EntityMSG("0")});
        super.fireTimedEvent(TORPEDO_LOCATION_MSG,
                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
                this.simulator.getSimTime());
    }
}
