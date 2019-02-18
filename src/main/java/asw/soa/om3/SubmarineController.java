package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

import java.util.HashMap;

public class SubmarineController {

    private int weaponCounts = 2;

    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private SubmarineManeuver maneuver = null;

    private TorpedoSensor _t1 = null;
    private TorpedoSensor _t2 = null;

    private HashMap<String, String> LockedTarget = new HashMap<String, String>();

    public SubmarineController(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }

    public synchronized void decide(final ModelData data, final EntityMSG object) throws SimRuntimeException {
        if(object.name.equals("0")){
            data.lineData.reset();
        }
        else{
            // 设置通信线数据
            data.lineData.updateData(data.origin.x, data.origin.y, object.x, object.y);
            // 施放鱼雷，对同一目标仅施放一个鱼雷
            if (!LockedTarget.containsKey(object.name)) {
                if (weaponCounts == 2) {
                    try {
                        //_t1.set_mdata(data);
                        //_t1 = new TorpedoSensor(this.simulator);
                        if(_t1 != null){
                            this.simulator.scheduleEventRel(2.0, this, _t1, "fire", new Object[] { data,object });
                            weaponCounts--;
                            LockedTarget.put(object.name, object.name);
                        }

                    } catch (SimRuntimeException e) {
                        SimLogger.always().error(e);
                    }
                } else if (weaponCounts == 1) {
                    try {
                        //_t2.set_mdata(data);
                        //_t2 = new TorpedoSensor(this.simulator);
                        if(_t2 != null){
                            this.simulator.scheduleEventRel(2.0, this, _t2, "fire", new Object[] { data,object });
                            LockedTarget.put(object.name, object.name);
                            weaponCounts--;
                        }

                    } catch (SimRuntimeException e) {
                        SimLogger.always().error(e);
                    }
                } else {
                    // 逃逸
                }
            }
        }
        if(maneuver!= null)
            this.simulator.scheduleEventRel(3.0,this, maneuver, "update", new Object[]{ data,object });
    }

    public TorpedoSensor get_t1() {
        return _t1;
    }

    public void set_t1(TorpedoSensor _t1) {
        this._t1 = _t1;
    }

    public TorpedoSensor get_t2() {
        return _t2;
    }

    public void set_t2(TorpedoSensor _t2) {
        this._t2 = _t2;
    }

    public SubmarineManeuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(SubmarineManeuver maneuver) {
        this.maneuver = maneuver;
    }
}
