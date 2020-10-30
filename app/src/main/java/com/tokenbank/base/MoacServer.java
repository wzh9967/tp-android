package com.tokenbank.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.tokenbank.config.Constant;
import com.tokenbank.utils.NetUtil;

/**
 * 用来检测节点的连接性
 */
public class MoacServer {
    private static String node ;
    private static String ping ;
    private static MoacServer instance;
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
    public String getPing(){
        return this.ping;
    }

    public void setNode(String node,String ping){
        this.node = node;
        this.ping = ping;
    }
}
