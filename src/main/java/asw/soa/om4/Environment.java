package asw.soa.om4;

import asw.soa.data.EntityMSG;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Environment extends DeliveryBase {

    public static final EventType ENV_INFO = new EventType("ENT_INFO");

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public Environment(DEVSSimulatorInterface.TimeDouble simulator)
    {
        this.simulator = simulator;
    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        System.out.println("====================="+event.getType());
        EntityMSG tmp = (EntityMSG) event.getContent();
        if(tmp != null){
            this.fireTimedEvent(ENV_INFO, new EntityMSG(tmp),this.simulator.getSimTime());
        }
    }
}
