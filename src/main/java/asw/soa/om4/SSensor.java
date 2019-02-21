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

public class SSensor extends DeliveryBase {

    private String name;

    public static final EventType THREAT_INFO = new EventType("THREAT_INFO");

    private MoveResult currentPos;

    private DEVSSimulatorInterface.TimeDouble simulator;

    private double detectRange;

    private double sigma;

    private ENT_INFO target;

    public SSensor(String name, final DEVSSimulatorInterface.TimeDouble simulator, final double detectRange, final double sigma) {
        this.name = name;
        this.simulator = simulator;
        this.detectRange = detectRange;
        this.sigma = sigma;
        target = new ENT_INFO();
        currentPos = new MoveResult();

    }

    public synchronized void next() throws SimRuntimeException {
        //周期调度实现机动：
        castThreatInfo(target);
        this.simulator.scheduleEventRel(this.sigma, this, this, "next", null);
    }

    @Override
    public synchronized void notify(EventInterface event) throws RemoteException {

        if (event.getType() == Submarine.THREAT_ENT_INFO) {
            ENT_INFO ent = (ENT_INFO) event.getContent();
            if (ent.belong == 1) {
                double distance = SimUtil.calcLength(currentPos.x, currentPos.y, ent.x, ent.y);
                if (distance < this.detectRange)
                    target = ent;
            }
        } else if (event.getType() == SManeuver.MOVE_RESULT) {
            //传感器接收自己的机动信息，决策依据：
            currentPos = (MoveResult) event.getContent();
        }
    }

    private synchronized void castThreatInfo(ENT_INFO ent) {
        ent.senderId = this.name;
        super.fireTimedEvent(SSensor.THREAT_INFO, ent, this.simulator.getSimTime());
    }

    public synchronized void Run() {
        try {
            next();
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }

    public double getDetectRange() {
        return detectRange;
    }

    public void setDetectRange(double detectRange) {
        this.detectRange = detectRange;
    }
}
