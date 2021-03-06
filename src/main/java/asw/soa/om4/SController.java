package asw.soa.om4;

import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveCmd;
import asw.soa.om4.message.MoveResult;
import asw.soa.om4.message.ThreatInfo;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class SController extends DeliveryBase {

    private String name;

    public static final EventType MOVE_CMD = new EventType("MOVE_CMD");

    public static final EventType WP_LAUNCH = new EventType("WP_LAUNCH");

    private MoveResult currentPos;

    private ThreatInfo target;

    private double sigma;

    private DEVSSimulatorInterface.TimeDouble simulator;

    public SController(String name, final DEVSSimulatorInterface.TimeDouble simulator, final double sigma) {
        this.name = name;
        this.simulator = simulator;
        this.sigma = sigma;
        this.currentPos = new MoveResult();
        this.target = new ThreatInfo();
    }

    private synchronized void next() {

        try {
            if (target.name.equals("0") || currentPos.name.equals("0")) {

            } else {
                castMOVE_CMD(currentPos, target);
            }
            this.simulator.scheduleEventAbs(this.simulator.getSimulatorTime() + this.sigma, this, this, "next", null);
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        //System.out.println("==="+this.currentPos.name + "  ==  "+event.getType());

        if (event.getType() == SSensor.THREAT_INFO) {
            target = new ThreatInfo((ENT_INFO) event.getContent());

        } else if (event.getType() == SManeuver.MOVE_RESULT) {
            //控制器接收自己的机动信息，决策依据
            currentPos = (MoveResult) event.getContent();
        }
    }

    private synchronized void castMOVE_CMD(MoveResult currentPos, ThreatInfo info) {
        MoveCmd msg = new MoveCmd(currentPos, info, "follow");
        msg.senderId = this.name;
        super.fireTimedEvent(MOVE_CMD, msg, this.simulator.getSimTime());
    }

    public synchronized void Run() {
        next();
    }
}
