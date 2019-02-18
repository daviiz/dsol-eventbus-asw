package asw.soa.om4.mssage;

public class MoveCmd implements java.io.Serializable {

    public String cmd;

    public MoveCmd(String cmd){
        this.cmd = cmd;
    }

    public MoveCmd(){
        this.cmd = "";
    }
}
