package asw.soa.om4;

import asw.soa.om4.mssage.ENT_INFO;
import asw.soa.om4.mssage.MoveCmd;
import asw.soa.om4.mssage.MoveResult;
import asw.soa.om4.mssage.ThreatInfo;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class SController extends DeliveryBase {

    private String name = "FController";

    public static final EventType MOVE_CMD = new EventType("MOVE_CMD");

    public static final EventType WP_LAUNCH = new EventType("WP_LAUNCH");

    private MoveResult currentPos = new MoveResult();

    private ThreatInfo target = new ThreatInfo();

    private double sigma = 5;

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public SController(String name, final DEVSSimulatorInterface.TimeDouble simulator, final double sigma) {
        this.name = name;
        this.simulator = simulator;
        this.sigma = sigma;
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
        if (target.name.equals("0") || currentPos.name.equals("0"))
            return;
        try {
            this.simulator.scheduleEventAbs(this.simulator.getSimulatorTime() + this.sigma, this, this, "castMOVE_CMD", new Object[]{currentPos, target});
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }

    }

    private synchronized void castMOVE_CMD(MoveResult currentPos, ThreatInfo info) {
        super.fireTimedEvent(MOVE_CMD, new MoveCmd(currentPos, info, "follow"), this.simulator.getSimTime());
    }
}
