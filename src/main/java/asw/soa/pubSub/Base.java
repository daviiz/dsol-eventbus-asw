package asw.soa.pubSub;

import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.event.EventProducer;

import java.rmi.RemoteException;

/**
 *
 */
public abstract class Base extends EventProducer implements EventListenerInterface {

    @Override
    public abstract void notify(EventInterface event) throws RemoteException;
}
