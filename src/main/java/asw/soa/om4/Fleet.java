package asw.soa.om4;

import asw.soa.data.ModelData;
import asw.soa.om4.message.ENT_INFO;
import asw.soa.om4.message.MoveResult;
import asw.soa.pubSub.DeliveryBase;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Fleet extends DeliveryBase {

    public static final EventType FLEET_ENT_INFO = new EventType("FLEET_ENT_INFO");
    public static final EventType THREAT_ENT_INFO = new EventType("THREAT_ENT_INFO");

    private String name;
    private FSensor fleetFSensor;
    private FController fleetFController;
    private FManeuver fleetFManeuver;

    private ModelData data;

    private DEVSSimulatorInterface.TimeDouble simulator;

    public Fleet(String name,final DEVSSimulatorInterface.TimeDouble simulator, ModelData data, double sigma) {
        this.simulator = simulator;
        this.data = data;
        this.name = name;
        //原子模型实例化：
        fleetFManeuver = new FManeuver(this.name, simulator, data, sigma);
        fleetFController = new FController(this.name, simulator, sigma);
        fleetFSensor = new FSensor(this.name, simulator, data.detectRange, sigma);

        //事件发布订阅：模型之间的数据交换
        fleetFManeuver.addListener(this, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFSensor, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFController, FManeuver.MOVE_RESULT);
        fleetFSensor.addListener(fleetFController, FSensor.THREAT_INFO);
        fleetFController.addListener(fleetFManeuver, FController.MOVE_CMD);

        this.addListener(fleetFSensor, Fleet.THREAT_ENT_INFO);
    }

    /**
     * @param event
     * @throws RemoteException
     */
    @Override
    public void notify(EventInterface event) throws RemoteException {
        if (event.getType() == FManeuver.MOVE_RESULT) {
            MoveResult info = (MoveResult) event.getContent();
            info.senderId = this.name;
            super.fireTimedEvent(FLEET_ENT_INFO, new ENT_INFO(info), this.simulator.getSimTime());
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
        fleetFManeuver.Run();
        fleetFController.Run();
        fleetFSensor.Run();
    }
}
