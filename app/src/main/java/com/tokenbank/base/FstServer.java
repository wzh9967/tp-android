package com.tokenbank.base;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;

/**
 * 用来检测节点的连接性
 */
public class FstServer {
    private static String node ;
    private static FstServer instance;
    private static int index = -1;
    private FstServer() {
    }
    public static FstServer getInstance() {
        if (instance == null) {
            instance = new FstServer();
        }
        return instance;
    }
    public void initNode(){
        node = Constant.fst_node;
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
