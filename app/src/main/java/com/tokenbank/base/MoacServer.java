package com.tokenbank.base;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;

/**
 * 用来检测节点的连接性
 */
public class MoacServer {
    private static String node ;
    private static MoacServer instance;
    private static int index = -1;
    private MoacServer() {
    }
    public static MoacServer getInstance() {
        if (instance == null) {
            instance = new MoacServer();
        }
        return instance;
    }
    public void initNode(){
        node = Constant.moc_node;
    }

    public String getNode(){
        return this.node;
    }
    public int getIndex(){
        return this.index;
    }
    public void setNode(GsonUtil node){
        this.node = node.getString("node", "");
        this.index = node.getInt("position", 0);
    }
}
