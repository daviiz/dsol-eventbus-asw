package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

import javax.naming.NamingException;
import java.awt.*;
import java.rmi.RemoteException;

public class DecoyController {


    private DecoyManeuver maneuver = null;

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public DecoyController(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }
    /**
     * 接收雷达探测的信息，决策
     *
     * @param object
     * @throws RemoteException
     * @throws NamingException
     * @throws SimRuntimeException
     */
    public synchronized void decide(final ModelData data,final EntityMSG object) throws SimRuntimeException {

        if(!object.name.equals("0") ){
            double dis = SimUtil.calcLength(data.origin.x, data.origin.y, object.x, object.y);
            if (dis < SimUtil.hit_distance) {
                data.color = Color.BLACK;
                data.status = false;
            }
        }

        if(maneuver!= null)
            this.simulator.scheduleEventRel(3.0,this, maneuver, "next", new Object[]{ data,object });
    }

    public DecoyManeuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(DecoyManeuver maneuver) {
        this.maneuver = maneuver;
    }
}
