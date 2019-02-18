package asw.soa.om4;

import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Maneuver extends DeliveryBase {

    public static final EventType MOVE_FINISHED = new EventType("MOVE_FINISHED");

    public static final EventType MOVE_RESULT = new EventType("MOVE_RESULT");

    @Override
    public void notify(EventInterface event) throws RemoteException {

    }
}
