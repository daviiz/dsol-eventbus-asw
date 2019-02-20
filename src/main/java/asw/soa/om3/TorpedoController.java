package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

import javax.naming.NamingException;
import java.rmi.RemoteException;

public class TorpedoController {

    private TorpedoManeuver maneuver = null;

    private double lastDistance = 250.0;

    private EntityMSG lastTarget = null;

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public TorpedoController(final DEVSSimulatorInterface.TimeDouble simulator) {
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
    public synchronized void decide(final ModelData data, final EntityMSG object) throws SimRuntimeException {
        if (object.name.equals("0")) {
            data.lineData.reset();
            lastTarget = object;
        } else {
            double tmpL = SimUtil.calcLength(data.origin.x, data.origin.y, object.x, object.y);
            // 在探测范围内 并且是生存状态的实体才显示通信线
            if (data.status == true) {
                data.lineData.updateData(data.origin.x, data.origin.y, object.x, object.y);
            }
            // 在探测范围内 找到更近的 设置其为目标
            if (tmpL < lastDistance) {
                lastTarget = new EntityMSG(object);
                lastDistance = tmpL;
            }
            // 如果自己的目标已经死亡 在探测范围内寻找目标 找到就重新设置目标
            if (this.lastTarget.status == false) {
                lastDistance = tmpL;
                lastTarget = new EntityMSG(object);
            }
        }
        if (maneuver != null)
            this.simulator.scheduleEventRel(3.0, this, maneuver, "next", new Object[]{data, lastTarget});
    }


    public TorpedoManeuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(TorpedoManeuver maneuver) {
        this.maneuver = maneuver;
    }

    public EntityMSG getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(EntityMSG lastTarget) {
        this.lastTarget = lastTarget;
    }

}
