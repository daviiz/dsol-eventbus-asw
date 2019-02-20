package asw.soa.om4.message;

public class WpLaunch implements java.io.Serializable {

    /**
     * 发送者标识ID
     */
    public String senderId;

    public boolean wpLaunch = false;

    WpLaunch() {
    }

    public WpLaunch(boolean wpLaunch) {
        this.wpLaunch = wpLaunch;
    }

}
