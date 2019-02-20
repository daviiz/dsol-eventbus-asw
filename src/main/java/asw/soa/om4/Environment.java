package asw.soa.om4;

import asw.soa.om4.message.ENT_INFO;
import asw.soa.pubSub.DeliveryBase;
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
        //if (event.getType() == Fleet.FLEET_ENT_INFO || event.getType() == Submarine.SUBMARINE_ENT_INFO) {
            ENT_INFO info = (ENT_INFO) event.getContent();
            super.fireTimedEvent(ENV_INFO, info, this.simulator.getSimTime());
        //}
    }
}
