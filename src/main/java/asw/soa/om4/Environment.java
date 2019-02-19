package asw.soa.om4;

import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Environment extends DeliveryBase {

    private String name;

    public static final EventType ENV_INFO = new EventType("ENT_INFO");

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public Environment(String name, final DEVSSimulatorInterface.TimeDouble simulator) {
        this.name = name;
        this.simulator = simulator;
    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException {
        if (event.getType() == FManeuver.MOVE_RESULT || event.getType() == SManeuver.MOVE_RESULT) {
            MoveResult info = (MoveResult) event.getContent();
            try {
                this.simulator.scheduleEventNow(this, this, "castENVI_INFO", new Object[]{info});
            } catch (SimRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void castENVI_INFO(MoveResult data) {
        super.fireTimedEvent(ENV_INFO, new ENT_INFO(data), this.simulator.getSimTime());
    }
}
