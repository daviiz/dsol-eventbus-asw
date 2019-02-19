package asw.soa.om4.mssage;

public class MoveCmd implements java.io.Serializable {

    public String cmd;

    public MoveResult currentPos;
    public ThreatInfo threat;

    public MoveCmd(String cmd) {
        this.cmd = cmd;
    }

    public MoveCmd() {
        this.cmd = "0";
    }

    public MoveCmd(MoveResult currentPos, ThreatInfo info) {
        this.currentPos = currentPos;
        this.threat = info;
        this.cmd = "unfollow";
    }

    public MoveCmd(MoveResult currentPos, ThreatInfo info, String cmd) {
        this.currentPos = currentPos;
        this.threat = info;
        this.cmd = cmd;
    }
}
