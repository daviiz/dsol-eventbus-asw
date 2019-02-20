package asw.soa.om2;

import asw.soa.data.EntityMSG;
import asw.soa.data.ModelData;
import asw.soa.main.SimUtil;
import asw.soa.pubSub.DeliveryBase;
import asw.soa.view.Visual2dService;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

import java.awt.*;
import java.rmi.RemoteException;

/**
 * @author daiwenzhi
 */
public class Fleet extends DeliveryBase {

    private static final long serialVersionUID = 5337683693470946049L;

    private static final EventType FLEET_LOCATION_MSG = new EventType("FLEET_LOCATION_MSG");

    /**
     * the simulator.
     */
    private DEVSSimulatorInterface.TimeDouble simulator = null;


    /**
     * the stream -- ugly but works.
     */
    private static StreamInterface stream = new MersenneTwister();

    private Decoy _decoy1;

    private Decoy _decoy2;

    private int decoyCouts = 2;

    /**
     * 探测到的威胁实体信息
     */
    private EntityMSG lastThreat = null;

    private volatile boolean isDead = false;

    /**
     * asw策略设置：1：施放鱼雷并逃逸；2:逃逸
     */
    private int aswPolicy = 1;

    private ModelData _mdata = new ModelData();

//	public Fleet(String name, double x, double y, final DEVSSimulatorInterface.TimeDouble simulator)
//			throws RemoteException, SimRuntimeException {
//
//		_mdata.origin = new CartesianPoint(x, y, 0);
//		_mdata.destination = new CartesianPoint(x, y, 0);
//		this.simulator = simulator;
//		this._mdata.name = name;
//		_mdata.detectRange = 200;
//		_mdata.belong = 1;
//
//
//		_decoy1 = new Decoy(name + "_decoy1", x, y, simulator);
//		_decoy2 = new Decoy(name + "_decoy2", x, y, simulator);
//
//		this.next();
//	}

    public Fleet(ModelData _data, final DEVSSimulatorInterface.TimeDouble simulator)
            throws RemoteException, SimRuntimeException {
        this.simulator = simulator;
        this._mdata = _data;

        ModelData d1Data = new ModelData("Decoy_1_" + this._mdata.name);
        d1Data.origin = d1Data.destination = this._mdata.origin;
        _decoy1 = new Decoy(d1Data, simulator);
        ModelData d2Data = new ModelData("Decoy_2_" + this._mdata.name);
        d2Data.origin = d2Data.destination = this._mdata.origin;
        _decoy2 = new Decoy(d2Data, simulator);

        //事件注册：
        //EventBus.getDefault().register(_decoy1);
        //EventBus.getDefault().register(_decoy2);
        this.addListener(Environment.getInstance(), Fleet.FLEET_LOCATION_MSG);

        Environment.getInstance().addListener(_decoy1, Environment.ENVIRONMENT_SONAR_DETECTED);
        Environment.getInstance().addListener(_decoy2, Environment.ENVIRONMENT_SONAR_DETECTED);

        this.next();
    }

    /**
     * next movement.
     *
     * @throws RemoteException     on network failure
     * @throws SimRuntimeException on simulation failure
     */
    private synchronized void next() throws RemoteException, SimRuntimeException {

        this._mdata.origin = this._mdata.destination;

        if (isDead) {
            this._mdata.destination = new CartesianPoint(this._mdata.destination.x, this._mdata.destination.y, 0);
        } else if (lastThreat == null) {
            this._mdata.destination = new CartesianPoint(this._mdata.destination.x + this._mdata.speed, this._mdata.destination.y + this._mdata.speed,
                    0);
        } else {
            this._mdata.destination = SimUtil.nextPoint(this._mdata.origin.x, this._mdata.origin.y, lastThreat.x,
                    lastThreat.y, this._mdata.speed, false);
        }

        this._mdata.startTime = this.simulator.getSimulatorTime();
        this._mdata.stopTime = this._mdata.startTime + Math.abs(new DistNormal(stream, 9, 1.8).draw());

        // System.out.println(Math.abs(new DistNormal(stream, 9, 1.8).draw()));

        this.simulator.scheduleEventAbs(this._mdata.stopTime, this, this, "next", null);
        super.fireTimedEvent(FLEET_LOCATION_MSG,
                new EntityMSG(_mdata.name, _mdata.belong, _mdata.status, this._mdata.origin.x, this._mdata.origin.y),
                this.simulator.getSimTime().plus(2.0));
        //EventBus.getDefault().post(new EntityEvent(_mdata.name, _mdata.belong, _mdata.status, this._mdata.origin.x, this._mdata.origin.y));

    }

    @Override
    public synchronized void notify(final EventInterface event) {
        if (!isDead) {
            EntityMSG tmp = (EntityMSG) event.getContent();
            if (tmp.belong == this._mdata.belong) {

                // System.out.println(name + " received msg: " + tmp.name + " current
                // location:x=" + tmp.x + ", y=" + tmp.y);

                // fireTimedEvent(Fleet.FLEET_LOCATION_UPDATE_EVENT, (LOC)event.getContent(),
                // this.simulator.getSimulatorTime());

            } else if (tmp.belong != this._mdata.belong) {
                //EntityMSG tmp = (EntityMSG) event.getContent();
                // System.out.println(name + " received msg: " + tmp.name + " current
                // location:x=" + tmp.x + ", y=" + tmp.y);
                double dis = SimUtil.calcLength(this._mdata.origin.x, this._mdata.origin.y, tmp.x, tmp.y);

                if (dis < _mdata.detectRange) {
                    if (aswPolicy == 1) {
                        if (decoyCouts == 2) {
                            try {
                                _decoy1.setLocation(this._mdata.origin);
                                this.simulator.scheduleEventRel(20.0, this, _decoy1, "fire", new Object[]{tmp});
                                decoyCouts--;

                            } catch (SimRuntimeException e) {
                                SimLogger.always().error(e);
                            }
                        } else if (decoyCouts == 1) {
                            try {
                                _decoy2.setLocation(this._mdata.origin);
                                this.simulator.scheduleEventRel(120.0, this, _decoy2, "fire", new Object[]{tmp});
                                decoyCouts--;
                            } catch (SimRuntimeException e) {
                                SimLogger.always().error(e);
                            }
                        }
                    }
                    lastThreat = tmp;
                    if (dis < SimUtil.hit_distance) {
                        // visualComponent.setColor(Color.BLACK);
                        _mdata.color = Color.BLACK;
                        Visual2dService.getInstance().update(this._mdata);
                        isDead = true;
                        _mdata.status = false;
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
            	if (!isDead) {
        			if (event.name.startsWith("Fleet_")) {
        				// System.out.println(name + " received msg: " + tmp.name + " current
        				// location:x=" + tmp.x + ", y=" + tmp.y);

        				// fireTimedEvent(Fleet.FLEET_LOCATION_UPDATE_EVENT, (LOC)event.getContent(),
        				// this.simulator.getSimulatorTime());

        			} else if (event.name.startsWith("Torpedo_")) {
        				// System.out.println(name + " received msg: " + tmp.name + " current
        				// location:x=" + tmp.x + ", y=" + tmp.y);
        				double dis = SimUtil.calcLength(_mdata.origin.x, _mdata.origin.y, event.x, event.y);

        				if (dis < _mdata.detectRange) {
        					if (aswPolicy == 1) {
        						if (decoyCouts == 2) {
        							try {
        								_decoy1.setLocation(_mdata.origin);
        								simulator.scheduleEventRel(20.0, this, _decoy1, "fire", new Object[] { new EntityMSG(event) });
        								decoyCouts--;

        							} catch (SimRuntimeException e) {
        								SimLogger.always().error(e);
        							}
        						} else if (decoyCouts == 1) {
        							try {
        								_decoy2.setLocation(_mdata.origin);
        								simulator.scheduleEventRel(120.0, this, _decoy2, "fire", new Object[] { new EntityMSG(event) });
        								decoyCouts--;
        							} catch (SimRuntimeException e) {
        								SimLogger.always().error(e);
        							}
        						}
        					}
        					lastThreat = new EntityMSG(event);
        					if (dis < SimUtil.hit_distance) {
        						// visualComponent.setColor(Color.BLACK);
        						_mdata.color = Color.BLACK;
        						Visual2dService.getInstance().update(_mdata);
        						isDead = true;
        						_mdata.status = false;
        					}
        				}

        			}
        		}
            }
        });
	}
	*/
}
