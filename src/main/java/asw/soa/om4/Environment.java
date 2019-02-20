package asw.soa.om4;

import asw.soa.om4.message.ENT_INFO;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Environment extends DeliveryBase {

    public static final EventType ENV_INFO = new EventType("ENT_INFO");

    private String name;
    private DEVSSimulatorInterface.TimeDouble simulator;

    public Environment(String name, final DEVSSimulatorInterface.TimeDouble simulator) {
        this.name = name;
        this.simulator = simulator;
    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException {
        ENT_INFO info = (ENT_INFO) event.getContent();
        if (info.name.equals("0")) return;
        info.senderId = this.name;
        super.fireTimedEvent(ENV_INFO, info, this.simulator.getSimTime());
    }
}
