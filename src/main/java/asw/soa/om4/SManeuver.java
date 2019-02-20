package asw.soa.om4;

import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveCmd;
import asw.soa.om4.message.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import java.rmi.RemoteException;

public class SManeuver extends DeliveryBase {

    //public static final EventType ENV_INFO = new EventType("ENV_INFO");
    private String name = "FManeuver";

    public static final EventType MOVE_RESULT = new EventType("MOVE_RESULT");

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private ModelData data = null;

    private ENT_INFO target = new ENT_INFO();

    private MoveCmd moveCmd = new MoveCmd();

    private double sigma = 10.0;

    public SManeuver(String name, final DEVSSimulatorInterface.TimeDouble simulator, final ModelData data, double sigma) {

        this.name = name;
        this.simulator = simulator;
        this.data = data;

        this.sigma = sigma;
    }

    public synchronized void next() throws SimRuntimeException {

        this.data.origin = this.data.destination;

        if (!this.data.status) {
            this.data.destination = new CartesianPoint(data.destination.x, data.destination.y, 0);
        } else if (this.target.name.equals("0")) {
            data.destination = new CartesianPoint(data.destination.x + data.speed, data.destination.y + data.speed,
                    0);
        } else {

            boolean isFollow = this.moveCmd.cmd.equals("follow");
            //System.out.println(data.name+"----------"+isFollow+"--------------"+target.name);
            data.destination = SimUtil.nextPoint(data.origin.x, data.origin.y, target.x,
                    target.y, data.speed, isFollow);
        }
        data.startTime = this.simulator.getSimulatorTime();
        data.stopTime = data.startTime + this.sigma;
        //周期调度实现机动：
        this.simulator.scheduleEventAbs(data.stopTime, this, this, "next", null);
        //输出机动结果消息
        //this.simulator.scheduleEventAbs(data.stopTime, this, this, "castMoveResult", new Object[]{data});
        castMoveResult(data);

        data.lineData.reset();
    }

    private synchronized void castMoveResult(ModelData data) {
        MoveResult msg = new MoveResult(data.name, data.belong, data.origin.x, data.origin.y, 0);
        msg.senderId = this.name;
        super.fireTimedEvent(MOVE_RESULT,msg,this.simulator.getSimTime());
    }


    public synchronized void Run() {
        try {
            next();
        } catch (SimRuntimeException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void notify(EventInterface event) throws RemoteException {

        //System.out.println("==="+this.data.name + "  ==  "+event.getType());

        if (event.getType() == SController.MOVE_CMD) {
            this.setMoveCmd((MoveCmd) event.getContent());

            this.target = new ENT_INFO(this.moveCmd.threat);

            // 设置通信线数据
            data.lineData.updateData(data.origin.x, data.origin.y, target.x, target.y);


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
