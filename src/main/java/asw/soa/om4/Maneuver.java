package asw.soa.om4;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om4.mssage.ENT_INFO;
import asw.soa.om4.mssage.MoveCmd;
import asw.soa.om4.mssage.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import java.rmi.RemoteException;

public class Maneuver extends DeliveryBase {

    //public static final EventType MOVE_FINISHED = new EventType("MOVE_FINISHED");

    public static final EventType MOVE_RESULT = new EventType("MOVE_RESULT");

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private ModelData data = null;

    private ENT_INFO target = new ENT_INFO();

    private MoveCmd moveCmd = new MoveCmd();

    private double sigma = 10.0;

    public Maneuver(final DEVSSimulatorInterface.TimeDouble simulator, final ModelData data, ENT_INFO target, MoveCmd moveCmd,double sigma){
        this.simulator = simulator;
        this.data = data;
        this.target = target;
        this.moveCmd = moveCmd;
        this.sigma = sigma;
        try {
            next();
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }

    public synchronized void next() throws SimRuntimeException {
        this.data.origin = this.data.destination;

        if (!this.data.status) {
            this.data.destination = new CartesianPoint(data.destination.x, data.destination.y, 0);
        } else if (this.target.name.equals("0")) {
            data.destination = new CartesianPoint(data.destination.x + data.speed, data.destination.y + data.speed,
                    0);
        } else {
            data.destination = SimUtil.nextPoint(data.origin.x, data.origin.y, target.x,
                    target.y, data.speed, this.moveCmd.cmd.equals("follow"));
        }

        data.startTime = this.simulator.getSimulatorTime();
        data.stopTime = data.startTime + this.sigma;
        this.simulator.scheduleEventAbs(data.stopTime, this, this, "next", null);

        this.simulator.scheduleEventNow(this,this,"castMoveResult",new Object[]{data});
    }

    public synchronized  void castMoveResult(ModelData data){
        super.fireTimedEvent(MOVE_RESULT,
                new MoveResult(data.name,data.belong, data.origin.x, data.origin.y,0),
                this.simulator.getSimTime());
    }


    @Override
    public void notify(EventInterface event) throws RemoteException {
        if(event.getType() == Controller.MOVE_CMD){
            this.setMoveCmd((MoveCmd)event.getContent());
        }
    }

    public void setData(ModelData data) {
        this.data = data;
    }

    public void setTarget(ENT_INFO target) {
        this.target = target;
    }

    public void setMoveCmd(MoveCmd moveCmd) {
        this.moveCmd = moveCmd;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}
