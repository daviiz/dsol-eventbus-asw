package asw.soa.om4.message;

public class MoveResult implements java.io.Serializable {
    /**
     * 模型实体位置 -x
     */
    public double x;
    /**
     * 模型实体位置 -y
     */
    public double y;
    /**
     * 模型实体位置 -z
     */
    public double z;

    public int belong;

    /**
     * 模型实体名称
     */
    public String name;

    public MoveResult() {
        this.name = "0";
    }

    public MoveResult(String name, int belong, double x, double y, double z) {
        this.name = name;
        this.belong = belong;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
