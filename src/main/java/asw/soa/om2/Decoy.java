package asw.soa.om2;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.pubSub.DeliveryBase;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import javax.naming.NamingException;
import java.awt.*;
import java.rmi.RemoteException;

/**
 * 鱼雷诱饵模型
 *
 * @author daiwenzhi
 */
public class Decoy extends DeliveryBase {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final EventType DECOY_LOCATION_MSG = new EventType("DECOY_LOCATION_MSG");

    private boolean isFired = false;

    private EntityMSG lastThreat = null;

    /**
     * the stream -- ugly but works.
     */
    private static StreamInterface stream = new MersenneTwister();

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;

    // private volatile boolean isDead = false;

    private ModelData _mdata = new ModelData();
    ;

    public Decoy(ModelData data, final DEVSSimulatorInterface.TimeDouble simulator) {
        _mdata = data;
        this.simulator = simulator;
        //事件注册：
        this.addListener(Environment.getInstance(),Decoy.DECOY_LOCATION_MSG);

    }

    public Decoy(String string, double x, double y, final DEVSSimulatorInterface.TimeDouble simulator) {
        _mdata.name = string;
        _mdata.origin = _mdata.destination = new CartesianPoint(x, y, 0);
        this.simulator = simulator;

        //this.addListener(this,ENVIRONMENT_SONAR_DETECTED);
    }

	@Override
	public void notify(EventInterface event){
		if (isFired && (_mdata.status)) {
            EntityMSG tmp = (EntityMSG) event.getContent();
			if (tmp.belong != this._mdata.belong) {

				// System.out.println(name+" received msg: "+tmp.name+" current
				// location:x="+tmp.x+", y="+tmp.y);
				double dis = SimUtil.calcLength(this._mdata.origin.x, this._mdata.origin.y, tmp.x, tmp.y);
				if (dis < this._mdata.detectRange) {
					lastThreat = tmp;
					if (dis < SimUtil.hit_distance) {
						_mdata.color = Color.BLACK;
						// isDead = true;
						_mdata.status = false;
						Visual2dService.getInstance().update(_mdata);
					}
				}

			}
		}

	}
    /*
	@Subscribe
	public synchronized void onEntityEvent(EntityEvent event) throws SimRuntimeException {
		this.simulator.scheduleEventAbs(this.simulator.getSimTime().plus(2.0), new Executable()
        {
            @Override
            public void execute()
            {
            	if (isFired && (_mdata.status)) {
        			if (event.name.startsWith("Torpedo_")) {
        				double dis = SimUtil.calcLength(_mdata.origin.x, _mdata.origin.y, event.x, event.y);
        				if (dis < _mdata.detectRange) {
        					lastThreat = new EntityMSG(event);
        					if (dis < SimUtil.hit_distance) {
        						_mdata.color = Color.BLACK;
        						// isDead = true;
        						_mdata.status = false;
        						Visual2dService.getInstance().update(_mdata);
        					}
        				}

        			}
        		}
            }
        });
	}
	*/

    /**
     * 鱼雷诱饵施放
     *
     * @param object
     * @throws RemoteException
     * @throws NamingException
     * @throws SimRuntimeException
     */
    public synchronized void fire(final EntityMSG object) throws RemoteException, NamingException, SimRuntimeException {
        isFired = true;
        lastThreat = object;
        // 视图组件注册：
        Visual2dService.getInstance().register(this._mdata.name, simulator, this._mdata);
        next();
    }

    /**
     * next movement.
     *
     * @throws RemoteException     on network failure
     * @throws SimRuntimeException on simulation failure
     * @throws NamingException
     */
    private synchronized void next() throws RemoteException, SimRuntimeException, NamingException {

        this._mdata.origin = this._mdata.destination;
        // this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 +
        // stream.nextInt(0, 200), 0);
        // this.destination = new CartesianPoint(this.destination.x+4,
        // this.destination.y+4, 0);
        if (!_mdata.status) {
            this._mdata.destination = new CartesianPoint(this._mdata.destination.x, this._mdata.destination.y, 0);
        } else if (lastThreat == null) {
            // this.destination = new CartesianPoint(this.destination.x, this.destination.y,
            // 0);
        } else {
            this._mdata.destination = SimUtil.nextPoint(this._mdata.origin.x, this._mdata.origin.y, lastThreat.x,
                    lastThreat.y, this._mdata.speed, false);
        }
        this._mdata.startTime = this.simulator.getSimulatorTime();
        this._mdata.stopTime = this._mdata.startTime + Math.abs(new DistNormal(stream, 9, 1.8).draw());
        this.simulator.scheduleEventAbs(this._mdata.stopTime, this, this, "next", null);

		super.fireTimedEvent(DECOY_LOCATION_MSG,
				new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, this._mdata.origin.x, this._mdata.origin.y),
				this.simulator.getSimTime().plus(2.0));

        //EventBus.getDefault().post(new EntityEvent(_mdata.name, _mdata.belong, _mdata.status, this._mdata.origin.x, this._mdata.origin.y));

    }

    public void setLocation(CartesianPoint _origin) {
        this._mdata.origin = _origin;
        this._mdata.destination = _origin;
    }
}
