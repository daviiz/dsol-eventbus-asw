package asw.soa.om4;

import asw.soa.main.SimUtil;
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

    private MoveResult currentPos = new MoveResult();

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private double detectRange = 400.0;

    private double sigma = 5.0;

    public Sensor(final DEVSSimulatorInterface.TimeDouble simulator,final double detectRange , final double sigma){
        this.simulator = simulator;
        this.detectRange = detectRange;
        this.sigma = sigma;
    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException{
        if(event.getType() == Environment.ENV_INFO){
            ENT_INFO ent = (ENT_INFO) event.getContent();
            if((!currentPos.name.equals("0")) || (currentPos.belong!= ent.belong)){
                try {
                    double distance = SimUtil.calcLength(currentPos.x,currentPos.y,ent.x,ent.y);
                    if(distance < this.detectRange)
                        this.simulator.scheduleEventRel(this.sigma,this,this,"exportThreatInfo",new Object[]{ent});
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

    public double getDetectRange() {
        return detectRange;
    }

    public void setDetectRange(double detectRange) {
        this.detectRange = detectRange;
    }
}
