package asw.soa.pubSub;

import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.event.EventProducer;

import java.rmi.RemoteException;

/**
 * 事件发布-订阅 基类
 * @author daiwenzhi
 * @date 2019年2月13日
 */
public abstract class DeliveryBase extends EventProducer implements EventListenerInterface {

    @Override
    public abstract void notify(EventInterface event) throws RemoteException;
}
