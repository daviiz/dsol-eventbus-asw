package asw.soa.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.event.EventType;

public class MessageRouter {

	private final Map<EventInterface, ArrayList<EventListenerInterface>> subscriptionsByEventType ;
	
	private static volatile MessageRouter instance;

	private MessageRouter() {
		
		subscriptionsByEventType = new HashMap<>();
		
	}

	public static MessageRouter getInstance() {
		if (instance == null) {
			synchronized (MessageRouter.class) {
				if (instance == null) {
					instance = new MessageRouter();
				}
			}
		}
		return instance;
	}
	public void register(EventInterface msg) {
		EventType eventType = msg.getType();
		ArrayList<EventListenerInterface> subscriptions = subscriptionsByEventType.get(eventType);
		synchronized (this) {
			if(subscriptions == null) {
				subscriptionsByEventType.put(msg, new ArrayList<EventListenerInterface>());
			}else {
				SimLogger.always().error(new Exception("no duplicate msgtype post !"));
			}
		}
		
	}
	public void subscribe(EventInterface msg,EventListenerInterface subscriber) {
        
		EventType eventType = msg.getType();
		ArrayList<EventListenerInterface> subscriptions = subscriptionsByEventType.get(eventType);
		synchronized (this) {
			if(subscriptions == null) {
				SimLogger.always().error(new Exception("no msgtype defined !"));
			}else {
				subscriptions.add(subscriber);
			}
		}
    }
}
