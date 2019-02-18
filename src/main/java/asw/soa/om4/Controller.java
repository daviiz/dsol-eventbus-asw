package asw.soa.om4;

import asw.soa.om4.mssage.ThreatInfo;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Controller extends DeliveryBase {

    public static final EventType MOVE_CMD = new EventType("MOVE_CMD");

    public static final EventType WP_LAUNCH = new EventType("WP_LAUNCH");

    private DEVSSimulatorInterface.TimeDouble simulator = null;
    
    public Controller(DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        if(event.getType() == Sensor.THREAT_INFO){
            ThreatInfo c = (ThreatInfo) event.getContent();
            System.out.println(c);
        }else if(event.getType() == Maneuver.MOVE_FINISHED){


        }else if(event.getType() == Maneuver.MOVE_RESULT){

        }

    }
}
