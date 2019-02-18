package asw.soa.om4.test;

import asw.soa.om4.Controller;
import asw.soa.om4.Sensor;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeClock;

public class TestPusSub {

    public static void main(String[] args) {
        DEVSRealTimeClock.TimeDouble simulator = new DEVSRealTimeClock.TimeDouble(0.1);
        Sensor s = new Sensor(simulator);
        Controller c = new Controller(simulator);
        s.addListener(c, Sensor.THREAT_INFO);

        s.pub();
    }

}
