package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.om2.Environment;
import asw.soa.om2.Fleet;
import asw.soa.om2.Submarine;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import javax.naming.NamingException;
import java.rmi.RemoteException;

/**
 * @author daiwenzhi
 */
public class ASWModel3 extends AbstractDSOLModel.TimeDouble<DEVSSimulatorInterface.TimeDouble> {
    /**
     * The default serial version UID for serializable classes.
     */
    private static final long serialVersionUID = 1L;

    private FleetSensor FleetSensor_1;
    private FleetController FleetController_1;
    private FleetManeuver FleetManeuver_1;

    private DecoySensor FS1_DecoySensor_1;
    private DecoyController FS1_DecoyController_1;
    private DecoyManeuver FS1_DecoyManeuver_1;

    private DecoySensor FS1_DecoySensor_2;
    private DecoyController FS1_DecoyController_2;
    private DecoyManeuver FS1_DecoyManeuver_2;


    private FleetSensor FleetSensor_2;
    private FleetController FleetController_2;
    private FleetManeuver FleetManeuver_2;

    private DecoySensor FS2_DecoySensor_1;
    private DecoyController FS2_DecoyController_1;
    private DecoyManeuver FS2_DecoyManeuver_1;

    private DecoySensor FS2_DecoySensor_2;
    private DecoyController FS2_DecoyController_2;
    private DecoyManeuver FS2_DecoyManeuver_2;

    private SubmarineSensor SubmarineSensor_1;
    private SubmarineController SubmarineController_1;
    private SubmarineManeuver SubmarineManeuver_1;

    private TorpedoSensor SS1_TorpedoSensor_1;
    private TorpedoController SS1_TorpedoController_1;
    private TorpedoManeuver SS1_TorpedoManeuver_1;

    private TorpedoSensor SS1_TorpedoSensor_2;
    private TorpedoController SS1_TorpedoController_2;
    private TorpedoManeuver SS1_TorpedoManeuver_2;


    /**
     * constructs a new BallModel.
     *
     * @param simulator the simulator
     */
    public ASWModel3(final DEVSSimulatorInterface.TimeDouble simulator) {
        super(simulator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void constructModel() throws SimRuntimeException {
        try {
            ModelData f1Data = new ModelData("Fleet_1");
            f1Data.origin = f1Data.destination = new CartesianPoint(-200, -50, 0);

            FS1_DecoySensor_1 = new DecoySensor(this.simulator);
            FS1_DecoyController_1 = new DecoyController(this.simulator);
            FS1_DecoyManeuver_1 = new DecoyManeuver(this.simulator);
            FS1_DecoyController_1.setManeuver(FS1_DecoyManeuver_1);
            FS1_DecoySensor_1.setController(FS1_DecoyController_1);

            FS1_DecoySensor_2 = new DecoySensor(this.simulator);
            FS1_DecoyController_2 = new DecoyController(this.simulator);
            FS1_DecoyManeuver_2 = new DecoyManeuver(this.simulator);
            FS1_DecoyController_2.setManeuver(FS1_DecoyManeuver_2);
            FS1_DecoySensor_2.setController(FS1_DecoyController_2);

            FleetSensor_1 = new FleetSensor(this.simulator);
            FleetController_1 = new FleetController(this.simulator);
            FleetManeuver_1 = new FleetManeuver(this.simulator);

            //FleetSensor_1.set_mdata(f1Data);
            FleetSensor_1.setController(FleetController_1);
            FleetController_1.setManeuver(FleetManeuver_1);
            FleetController_1.set_decoy1(FS1_DecoySensor_1);
            FleetController_1.set_decoy1(FS1_DecoySensor_2);
            FleetManeuver_1.addListener(Environment.getInstance(), FleetManeuver.FLEET_LOCATION_MSG);
            FleetManeuver_1.set_mdata(f1Data);

//            ModelData f2Data = new ModelData("Fleet_2");
//            f2Data.origin = f2Data.destination = new CartesianPoint(-250, 0, 0);
//            FleetSensor_2 = new FleetSensor(this.simulator);
//            FleetController_2 = new FleetController(this.simulator);
//            FleetManeuver_2 = new FleetManeuver(this.simulator);
//
//            FS2_DecoySensor_1 = new DecoySensor(this.simulator);
//            FS2_DecoyController_1 = new DecoyController(this.simulator);
//            FS2_DecoyManeuver_1 = new DecoyManeuver(this.simulator);
//            FS2_DecoyController_1.setManeuver(FS2_DecoyManeuver_1);
//            FS2_DecoySensor_1.setController(FS2_DecoyController_1);
//
//            FS2_DecoySensor_2 = new DecoySensor(this.simulator);
//            FS2_DecoyController_2 = new DecoyController(this.simulator);
//            FS2_DecoyManeuver_2 = new DecoyManeuver(this.simulator);
//            FS2_DecoyController_2.setManeuver(FS2_DecoyManeuver_2);
//            FS2_DecoySensor_2.setController(FS2_DecoyController_2);
//
//
//            FleetSensor_2.set_mdata(f2Data);
//            FleetSensor_2.setController(FleetController_2);
//            FleetController_2.setManeuver(FleetManeuver_2);
//            FleetController_2.set_decoy1(FS2_DecoySensor_1);
//            FleetController_2.set_decoy1(FS2_DecoySensor_2);
//            FleetManeuver_2.addListener(Environment.getInstance(), FleetManeuver.FLEET_LOCATION_MSG);
//            FleetManeuver_2.set_mdata(f2Data);
//
//            ModelData s1Data = new ModelData("Sub_1");
//            s1Data.origin = s1Data.destination = new CartesianPoint(200, 100, 0);
//            SubmarineSensor_1 = new SubmarineSensor(this.simulator);
//            SubmarineController_1 = new SubmarineController(this.simulator);
//            SubmarineManeuver_1 = new SubmarineManeuver(this.simulator);
//
//            SS1_TorpedoSensor_1 = new TorpedoSensor(this.simulator);
//            SS1_TorpedoController_1 = new TorpedoController(this.simulator);
//            SS1_TorpedoManeuver_1 = new TorpedoManeuver(this.simulator);
//            SS1_TorpedoController_1.setManeuver(SS1_TorpedoManeuver_1);
//            SS1_TorpedoSensor_1.setController(SS1_TorpedoController_1);
//
//            SS1_TorpedoSensor_2 = new TorpedoSensor(this.simulator);
//            SS1_TorpedoController_2 = new TorpedoController(this.simulator);
//            SS1_TorpedoManeuver_2 = new TorpedoManeuver(this.simulator);
//            SS1_TorpedoController_2.setManeuver(SS1_TorpedoManeuver_2);
//            SS1_TorpedoSensor_2.setController(SS1_TorpedoController_2);
//
//            SubmarineSensor_1.set_mdata(s1Data);
//            SubmarineSensor_1.setController(SubmarineController_1);
//            SubmarineController_1.setManeuver(SubmarineManeuver_1);
//            SubmarineController_1.set_t1(SS1_TorpedoSensor_1);
//            SubmarineController_1.set_t2(SS1_TorpedoSensor_2);
//            SubmarineManeuver_1.addListener(Environment.getInstance(), FleetManeuver.FLEET_LOCATION_MSG);
//            SubmarineManeuver_1.set_mdata(s1Data);



            // 视图组件注册：
            try {
                Visual2dService.getInstance().register(f1Data.name, simulator, f1Data);
                //Visual2dService.getInstance().register(f2Data.name, simulator, f2Data);
                //Visual2dService.getInstance().register(s1Data.name, simulator, s1Data);
            } catch (NamingException e) {
                SimLogger.always().error(e);
            }

        } catch (RemoteException exception) {
            SimLogger.always().error(exception);
        } finally {
            //FleetSensor_1.startSim();
            //FleetSensor_2.startSim();
           //SubmarineSensor_1.startSim();
            FleetManeuver_1.setLastThreat(new EntityMSG("0"));
            FleetManeuver_1.startSim();

//            FleetManeuver_2.setLastThreat(new EntityMSG("0"));
//            FleetManeuver_2.startSim();
//
//            SubmarineManeuver_1.setLastThreat(new EntityMSG("0"));
//            SubmarineManeuver_1.startSim();
        }
    }

}
