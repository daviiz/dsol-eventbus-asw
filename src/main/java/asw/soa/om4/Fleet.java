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


    private FSensor fleetFSensor;
    private FController fleetFController;
    private FManeuver fleetFManeuver;

    private ModelData data;
    private double sigma;

    private DEVSSimulatorInterface.TimeDouble simulator;

    public Fleet(final DEVSSimulatorInterface.TimeDouble simulator,ModelData data,double sigma){
        this.simulator = simulator;
        this.data = data;
        this.sigma = sigma;
        //原子模型实例化：
        fleetFManeuver = new FManeuver(this.data.name, simulator, data, sigma);
        fleetFController = new FController(this.data.name, simulator, sigma/2-0.05);
        fleetFSensor = new FSensor(this.data.name, simulator, data.detectRange, sigma/2-0.01);

        //事件发布订阅：模型之间的数据交换
        fleetFManeuver.addListener(this, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFSensor, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFController, FManeuver.MOVE_RESULT);
        fleetFSensor.addListener(fleetFController, FSensor.THREAT_INFO);
        fleetFController.addListener(fleetFManeuver, FController.MOVE_CMD);

        this.addListener(fleetFSensor,Fleet.THREAT_ENT_INFO);

    }

    /**
     *
     * @param event
     * @throws RemoteException
     */
    @Override
    public void notify(EventInterface event) throws RemoteException {
        if (event.getType() == FManeuver.MOVE_RESULT ) {
            MoveResult info = (MoveResult) event.getContent();
            super.fireTimedEvent(FLEET_ENT_INFO, new ENT_INFO(info), this.simulator.getSimTime());
        }
        if (event.getType() == Environment.ENV_INFO ) {
            ENT_INFO info = (ENT_INFO) event.getContent();
            super.fireTimedEvent(THREAT_ENT_INFO, new ENT_INFO(info), this.simulator.getSimTime());
        }
    }

    /**
     * 模型运行开始方法
     */
    public synchronized void Run() {
        fleetFManeuver.Run();
    }
}
