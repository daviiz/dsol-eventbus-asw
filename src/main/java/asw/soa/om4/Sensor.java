package asw.soa.om4;

import asw.soa.om4.mssage.ENT_INFO;
import asw.soa.om4.mssage.MoveResult;
import asw.soa.om4.mssage.ThreatInfo;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Sensor extends DeliveryBase {

    public static final EventType THREAT_INFO = new EventType("THREAT_INFO");

    private MoveResult currentPos = null;

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public Sensor(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;

    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException{
        if(event.getType() == Environment.ENV_INFO){
            ENT_INFO ent = (ENT_INFO) event.getContent();
            if(currentPos!=null){
                if(currentPos.name.equals(ent.name))
                    return;
                try {
                    this.simulator.scheduleEventRel(5.0,this,this,"exportThreatInfo",new Object[]{ent});
                } catch (SimRuntimeException e) {
                    e.printStackTrace();
                }

            }

        }else if(event.getType() == Maneuver.MOVE_RESULT){
            currentPos = (MoveResult)event.getContent();

        }
    }

    public synchronized void exportThreatInfo(ENT_INFO ent){
        super.fireTimedEvent(Sensor.THREAT_INFO,ent,this.simulator.getSimTime());
    }

    public void pub(){
        this.fireTimedEvent(THREAT_INFO,new ThreatInfo(),
                this.simulator.getSimTime());
    }
}
