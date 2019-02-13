package asw.soa.om2;

import asw.soa.data.EntityMSG;
import asw.soa.pubSub.Base;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventType;

import java.rmi.RemoteException;

public class Environment extends Base {

    /**
     *
     */
    public static final EventType ENVIRONMENT_SONAR_DETECTED = new EventType("ENVIRONMENT_SONAR_DETECTED");


    private static volatile Environment instance;

    private Environment() {
    }

    public static Environment getInstance() {
        if (instance == null) {
            synchronized (Environment.class) {
                if (instance == null) {
                    instance = new Environment();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param event
     * @throws RemoteException
     */
    @Override
    public void notify(EventInterface event) throws RemoteException {

        System.out.println("====================="+event.getType());
        EntityMSG tmp = (EntityMSG) event.getContent();
        this.fireEvent(ENVIRONMENT_SONAR_DETECTED, new EntityMSG(tmp));
    }
}
