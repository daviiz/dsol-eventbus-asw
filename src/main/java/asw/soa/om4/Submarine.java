package asw.soa.om4;

import asw.soa.data.ModelData;
import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Submarine extends DeliveryBase {

    public static final EventType SUBMARINE_ENT_INFO = new EventType("SUBMARINE_ENT_INFO");
    public static final EventType THREAT_ENT_INFO = new EventType("THREAT_ENT_INFO");

    private String name;
    private SSensor sensor;
    private SController controller;
    private SManeuver maneuver;

    private ModelData data;

    private DEVSSimulatorInterface.TimeDouble simulator;

    public Submarine(String name,final DEVSSimulatorInterface.TimeDouble simulator, ModelData data, double sigma) {
        this.name = name;
        this.simulator = simulator;
        this.data = data;
        //原子模型实例化：
        maneuver = new SManeuver(this.name, this.simulator, data, sigma);
        controller = new SController(this.name, simulator, sigma);
        sensor = new SSensor(this.name, simulator, data.detectRange, sigma);

        //事件发布订阅：模型之间的数据交换
        maneuver.addListener(this, FManeuver.MOVE_RESULT);
        maneuver.addListener(sensor, FManeuver.MOVE_RESULT);
        maneuver.addListener(controller, FManeuver.MOVE_RESULT);
        sensor.addListener(controller, FSensor.THREAT_INFO);
        controller.addListener(maneuver, FController.MOVE_CMD);

        this.addListener(sensor, Fleet.THREAT_ENT_INFO);

    }

    @Override
    public void notify(EventInterface event) throws RemoteException {
        if (event.getType() == SManeuver.MOVE_RESULT) {
            MoveResult info = (MoveResult) event.getContent();
            info.senderId = this.name;
            super.fireTimedEvent(SUBMARINE_ENT_INFO, new ENT_INFO(info), this.simulator.getSimTime());
        }
        if (event.getType() == Environment.ENV_INFO) {
            ENT_INFO info = (ENT_INFO) event.getContent();
            info.senderId = this.name;
            super.fireTimedEvent(THREAT_ENT_INFO, new ENT_INFO(info), this.simulator.getSimTime());
        }
    }

    /**
     * 模型运行开始方法
     */
    public synchronized void Run() {
        maneuver.Run();
        controller.Run();
        sensor.Run();
    }
}
