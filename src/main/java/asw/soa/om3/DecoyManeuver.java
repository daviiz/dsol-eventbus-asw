package asw.soa.om3;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventProducer;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import javax.naming.NamingException;
import java.rmi.RemoteException;

public class DecoyManeuver extends EventProducer {

    private static final EventType DECOY_LOCATION_MSG = new EventType("DECOY_LOCATION_MSG");

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    private boolean isFired = false;

    public DecoyManeuver(final DEVSSimulatorInterface.TimeDouble simulator){
        this.simulator = simulator;
    }

    /**
     * next movement.
     *
     * @throws RemoteException     on network failure
     * @throws SimRuntimeException on simulation failure
     * @throws NamingException
     */
    private synchronized void next(final ModelData _mdata, final EntityMSG lastThreat) throws SimRuntimeException {
        if(_mdata != null && (!isFired)){
            isFired = true;
            // 视图组件注册：
//            try {
//
//                Visual2dService.getInstance().register(_mdata.name, simulator, _mdata);
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            } catch (NamingException e) {
//                e.printStackTrace();
//            }
        }

        if (isFired){
            _mdata.origin = _mdata.destination;
            // this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 +
            // stream.nextInt(0, 200), 0);
            // this.destination = new CartesianPoint(this.destination.x+4,
            // this.destination.y+4, 0);
            if (!_mdata.status) {
                _mdata.destination = new CartesianPoint(_mdata.destination.x, _mdata.destination.y, 0);
            } else if (lastThreat == null) {
                // this.destination = new CartesianPoint(this.destination.x, this.destination.y,
                // 0);
            } else {
                _mdata.destination = SimUtil.nextPoint(_mdata.origin.x, _mdata.origin.y, lastThreat.x,
                        lastThreat.y, _mdata.speed, false);
            }
            _mdata.startTime = this.simulator.getSimulatorTime();
            _mdata.stopTime = _mdata.startTime + 7;
            this.simulator.scheduleEventAbs(_mdata.stopTime, this, this, "next", null);

            super.fireTimedEvent(DECOY_LOCATION_MSG,
                    new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, _mdata.origin.x, _mdata.origin.y),
                    this.simulator.getSimTime());
        }

    }
}
