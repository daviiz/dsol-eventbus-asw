package asw.soa.om4.mssage;

public class ThreatInfo implements java.io.Serializable {

    /**
     * 红蓝方归属：1 红方，0 中立方，-1 蓝方
     */
    public int belong;
    /**
     * 生存状态： true 生存，false 阵亡
     */
    public boolean status;
    /**
     * 模型实体位置 -x
     */
    public double x;
    /**
     * 模型实体位置 -y
     */
    public double y;

    /**
     * 模型实体名称
     */
    public String name;

    public ThreatInfo(){
        this.name = "0";
    }
    public ThreatInfo(String name){
        this.name = name;
    }

    public ThreatInfo(String name, int _belong, boolean _status, double _x, double _y) {
        this.name = name;
        this.belong = _belong;
        this.status = _status;
        this.x = _x;
        this.y = _y;
    }

    public ThreatInfo(ThreatInfo tmp) {
        this.name = tmp.name;
        this.belong = tmp.belong;
        this.status = tmp.status;
        this.x = tmp.x;
        this.y = tmp.y;
    }

}
