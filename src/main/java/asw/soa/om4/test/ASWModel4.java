package asw.soa.om4.test;

import asw.soa.data.ModelData;
import asw.soa.om4.*;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import javax.naming.NamingException;
import java.rmi.RemoteException;

public class ASWModel4 extends AbstractDSOLModel.TimeDouble<DEVSSimulatorInterface.TimeDouble> {

    FSensor fleetFSensor;
    SSensor submarineFSensor;
    FController fleetFController;
    SController submarineFController;
    FManeuver fleetFManeuver;
    SManeuver submarineFManeuver;
    Environment env;


    @Override
    public void constructModel() throws SimRuntimeException {
        //模型初始化：
        ModelData f1Data = new ModelData("Fleet_1");
        f1Data.origin = f1Data.destination = new CartesianPoint(-200, -50, 0);

        fleetFManeuver = new FManeuver("fleetFManeuver", this.simulator, f1Data, 10.0);
        fleetFController = new FController("fleetFController", this.simulator, 5.0);
        fleetFSensor = new FSensor("fleetFSensor", this.simulator, f1Data.detectRange, 5.0);

        ModelData s1Data = new ModelData("Sub_1");
        s1Data.origin = s1Data.destination = new CartesianPoint(200, 100, 0);

        submarineFManeuver = new SManeuver("submarineFManeuver", this.simulator, s1Data, 10.0);
        submarineFController = new SController("submarineFController", this.simulator, 5.0);
        submarineFSensor = new SSensor("submarineFSensor", this.simulator, s1Data.detectRange, 5.0);

        env = new Environment("env", this.simulator);

        //事件发布订阅：模型之间的数据交换
        fleetFManeuver.addListener(env, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFSensor, FManeuver.MOVE_RESULT);
        fleetFManeuver.addListener(fleetFController, FManeuver.MOVE_RESULT);
        fleetFSensor.addListener(fleetFController, FSensor.THREAT_INFO);
        fleetFController.addListener(fleetFManeuver, FController.MOVE_CMD);

        submarineFManeuver.addListener(env, SManeuver.MOVE_RESULT);
        submarineFManeuver.addListener(submarineFSensor, SManeuver.MOVE_RESULT);
        submarineFManeuver.addListener(submarineFController, SManeuver.MOVE_RESULT);
        submarineFSensor.addListener(submarineFController, SSensor.THREAT_INFO);
        submarineFController.addListener(submarineFManeuver, SController.MOVE_CMD);

        env.addListener(fleetFSensor, Environment.ENV_INFO);
        env.addListener(submarineFSensor, Environment.ENV_INFO);

        // 视图组件注册：
        try {
            Visual2dService.getInstance().register(f1Data.name, simulator, f1Data);
            Visual2dService.getInstance().register(s1Data.name, simulator, s1Data);

        } catch (NamingException e) {
            SimLogger.always().error(e);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //开始运行：
        submarineFManeuver.Run();
        fleetFManeuver.Run();
    }

    public ASWModel4(final DEVSSimulatorInterface.TimeDouble simulator) {
        super(simulator);
    }
}
