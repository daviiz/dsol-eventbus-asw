package asw.soa.om4;

import asw.soa.main.SimUtil;
import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class FSensor extends DeliveryBase {

    private String name = "FSensor";

    public static final EventType THREAT_INFO = new EventType("THREAT_INFO");

    private MoveResult currentPos = new MoveResult();

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private double detectRange = 400.0;

    private double sigma = 5.0;

    private ENT_INFO target;

    public FSensor(String name, final DEVSSimulatorInterface.TimeDouble simulator, final double detectRange, final double sigma) {
        this.name = name;
        this.simulator = simulator;
        this.detectRange = detectRange;
        this.sigma = sigma;
        target = new ENT_INFO();
        try {
            next();
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }

    public synchronized void next() throws SimRuntimeException {
        //周期调度实现机动：
        castThreatInfo(target);
        this.simulator.scheduleEventRel(this.sigma, this, this, "next", null);
    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException {
        //System.out.println("==="+this.currentPos.name + "  ==  "+event.getType());

        if (event.getType() == Fleet.THREAT_ENT_INFO) {
            ENT_INFO ent = (ENT_INFO) event.getContent();
            if ((!currentPos.name.equals("0")) && (currentPos.belong != target.belong)) {
                double distance = SimUtil.calcLength(currentPos.x, currentPos.y, target.x, target.y);
                if (distance < this.detectRange)
                    target = ent;
            }
        } else if (event.getType() == FManeuver.MOVE_RESULT) {
            //传感器接收自己的机动信息，决策依据：
            currentPos = (MoveResult) event.getContent();
        }

    }

    private synchronized void castThreatInfo(ENT_INFO ent) {
        super.fireTimedEvent(FSensor.THREAT_INFO, ent, this.simulator.getSimTime());
    }

    public double getDetectRange() {
        return detectRange;
    }

    public void setDetectRange(double detectRange) {
        this.detectRange = detectRange;
    }
}
