package asw.soa.om4.test;

import asw.soa.data.ModelData;
import asw.soa.om4.Environment;
import asw.soa.om4.Fleet;
import asw.soa.om4.Submarine;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import javax.naming.NamingException;
import java.rmi.RemoteException;

public class ASWModel4 extends AbstractDSOLModel.TimeDouble<DEVSSimulatorInterface.TimeDouble> {

    Fleet fleet1;
    Submarine sub1;
    Environment env;


    @Override
    public void constructModel() throws SimRuntimeException {
        //模型初始化：
        ModelData f1Data = new ModelData("Fleet_1");
        f1Data.origin = f1Data.destination = new CartesianPoint(-200, -50, 0);
        ModelData s1Data = new ModelData("Sub_1");
        s1Data.origin = s1Data.destination = new CartesianPoint(200, 100, 0);

        fleet1 = new Fleet(f1Data.name,this.simulator, f1Data, 20.0);
        sub1 = new Submarine(f1Data.name,this.simulator, s1Data, 10.0);
        env = new Environment("env", this.simulator);

        //事件发布订阅：模型之间的数据交换
        fleet1.addListener(env, Fleet.FLEET_ENT_INFO);
        sub1.addListener(env, Submarine.SUBMARINE_ENT_INFO);
        env.addListener(fleet1, Environment.ENV_INFO);
        env.addListener(sub1, Environment.ENV_INFO);

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
        fleet1.Run();
        sub1.Run();
    }

    public ASWModel4(final DEVSSimulatorInterface.TimeDouble simulator) {
        super(simulator);
    }
}
