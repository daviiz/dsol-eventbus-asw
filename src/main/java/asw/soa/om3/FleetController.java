package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

import java.awt.*;

public class FleetController {

    /**
     * asw策略设置：1：施放鱼雷并逃逸；2:逃逸
     */
    private int aswPolicy = 1;

    private DecoySensor _decoy1;

    private DecoySensor _decoy2;

    private int decoyCouts = 2;

    private FleetManeuver maneuver = null;

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    public FleetController(final DEVSSimulatorInterface.TimeDouble simulator) {
        this.simulator = simulator;
    }

    public synchronized void decide(final ModelData data, final EntityMSG object) throws SimRuntimeException {
        EntityMSG lastThreat = new EntityMSG("0");
        if (!object.name.equals("0")) {
            double dis = SimUtil.calcLength(data.origin.x, data.origin.y, object.x, object.y);
            if (aswPolicy == 1) {
                if (decoyCouts == 2) {
                    try {
                        //_decoy1.setLocation(this._mdata.origin);
                        //_decoy1 = new DecoySensor(this.simulator);
                        if (_decoy1 != null) {
                            //_decoy1.set_mdata(data);
                            this.simulator.scheduleEventRel(20.0, this, _decoy1, "fire", new Object[]{data, object});
                            decoyCouts--;
                        }

                    } catch (SimRuntimeException e) {
                        SimLogger.always().error(e);
                    }
                } else if (decoyCouts == 1) {
                    try {
                        //_decoy2.setLocation(this._mdata.origin);
                        //_decoy2 = new DecoySensor(this.simulator);
                        //_decoy2.set_mdata(data);
                        if (_decoy2 != null) {
                            this.simulator.scheduleEventRel(120.0, this, _decoy2, "fire", new Object[]{data, object});
                            decoyCouts--;
                        }
                    } catch (SimRuntimeException e) {
                        SimLogger.always().error(e);
                    }
                }
            }
            lastThreat = object;
            if (dis < SimUtil.hit_distance) {
                // visualComponent.setColor(Color.BLACK);
                data.color = Color.BLACK;
                Visual2dService.getInstance().update(data);
                //isDead = true;
                data.status = false;
            }
        }
        if (maneuver != null)
            this.simulator.scheduleEventRel(3.0, this, maneuver, "update", new Object[]{data, lastThreat});
    }

    public FleetManeuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(FleetManeuver maneuver) {
        this.maneuver = maneuver;
    }

    public DecoySensor get_decoy1() {
        return _decoy1;
    }

    public void set_decoy1(DecoySensor _decoy1) {
        this._decoy1 = _decoy1;
    }

    public DecoySensor get_decoy2() {
        return _decoy2;
    }

    public void set_decoy2(DecoySensor _decoy2) {
        this._decoy2 = _decoy2;
    }
}
