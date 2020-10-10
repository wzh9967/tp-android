package com.tokenbank.activity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import com.tokenbank.R;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.NodeCustomDialog;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.tokenbank.config.AppConfig.getContext;

/**
 * 初始化：
 * 1, 默认节点从本地获取         读取json
 * 2, 初始化存在sp中
 * 3, 如果不为空不重新加载, 加载以后ping测试
 * 4, ping不到到节点设置为不可点击
 * 5, ping到允许点击
 * 6, 点击过后设置为实际节点
 *
 * 设置节点
 * 1, 验证节点可连接性
 * 2, 初始化加入界面,和本地节点形式相同
 * 3, 更新list , 刷新显示框
 */
public class NodeSettingActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener{

    private static final String TAG = "NodeSettingActivity" ;
    private TitleBar mTitleBar;
    private RecyclerView mNodeRecyclerView;
    private int mSelectedItem = -1;
    private GsonUtil publicNodes = new GsonUtil("{}");
    private NodeRecordAdapter mAdapter;
    private Button mBtnAddNode;
    private int ConfirmNodeListLength = 0;
    private static CompositeDisposable compositeDisposable;
    private final static BigDecimal PING_QUICK = new BigDecimal("60");
    private final static BigDecimal PING_LOW = new BigDecimal("100");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_node);
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.title_SettingNode));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mBtnAddNode = findViewById(R.id.btn_node_setting);
        mBtnAddNode.setOnClickListener(this);

        //开始设置RecyclerView
        mNodeRecyclerView=this.findViewById(R.id.nodesetting_ecycleview);
        mNodeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NodeRecordAdapter();
        mNodeRecyclerView.setAdapter(mAdapter);
        getPublicNode();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_node_setting :
                new NodeCustomDialog(NodeSettingActivity.this, new NodeCustomDialog.onConfirmOrderListener() {
                    @Override
                    public void onConfirmOrder() {
                        Log.d(TAG, "onConfirmOrder: 设置用户节点");
                        getCustomNode();
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onLeftClick(View view) {
        this.finish();
    }

    @Override
    public void onRightClick(View view) {

    }

    @Override
    public void onMiddleClick(View view) {

    }

    public static void startNodeSettingActivity(Context from) {
        Intent intent = new Intent(from, NodeSettingActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    class NodeRecordAdapter extends RecyclerView.Adapter<NodeRecordAdapter.VH>{
        /**
         * 初始化控件
         */
        class VH extends RecyclerView.ViewHolder {
            RelativeLayout mLayoutItem;
            TextView mTvNodeUrl;
            TextView mTvNodeName;
            TextView mTvNodePing;
            ImageView mImgLoad;
            RadioButton mRadioSelected;
            ProgressDrawable mProgressDrawable;
            public VH(View itemView) {
                super(itemView);
                //设置栏目样式
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mTvNodeUrl = itemView.findViewById(R.id.tv_node_url);
                mTvNodeName = itemView.findViewById(R.id.tv_node_name);
                mTvNodePing = itemView.findViewById(R.id.tv_ping);
                mImgLoad = itemView.findViewById(R.id.img_ping);
                mRadioSelected = itemView.findViewById(R.id.radio_selected);
                mProgressDrawable = new ProgressDrawable();
                mProgressDrawable.setColor(0xff666666);
                mImgLoad.setImageDrawable(mProgressDrawable);
                mRadioSelected.setClickable(false);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VH vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                        int position = getAdapterPosition();
                        //重复点击
                        if (position == mSelectedItem) {
                            return;
                        } else if (position != mSelectedItem && vh != null) {
                            //切换
                            vh.mRadioSelected.setChecked(false);
                            vh.mLayoutItem.setActivated(false);
                            mSelectedItem = position;
                            vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                            vh.mRadioSelected.setChecked(true);
                            vh.mLayoutItem.setActivated(true);
                        } else {
                            //vh == null
                            if (mSelectedItem != -1) {
                                notifyItemChanged(mSelectedItem);
                            }
                            mSelectedItem = position;
                            vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(position);
                            vh.mRadioSelected.setChecked(true);
                            vh.mLayoutItem.setActivated(true);
                        }
                    }
                });
                mLayoutItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                        AlertDialog.Builder builder=new AlertDialog.Builder(NodeSettingActivity.this);
                        GsonUtil item = publicNodes.getObject(getAdapterPosition());
                        String node = item.getString("node","");
                        builder.setMessage("节点【"+node+"】将被删除");
                        builder.setTitle("提示");
                        //添加AlertDialog.Builder对象的setPositiveButton()方法
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position = getAdapterPosition();
                                DeleteNode(position);
                            }
                        });
                        //添加AlertDialog.Builder对象的setNegativeButton()方法
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                        return true;
                    }
                });
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_node, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, @SuppressLint("RecyclerView") int position) {
            if (publicNodes == null || publicNodes.getLength() == 0) {
                return;
            }
            GsonUtil item = publicNodes.getObject(position);
            holder.mTvNodeName.setText(item.getString("name", ""));
            String url = item.getString("node", "");
            holder.mTvNodeUrl.setText(url);
            holder.mLayoutItem.setClickable(true);
            holder.mProgressDrawable.start();
            String[] ws = url.replace("ws://", "").replace("wss://", "").split(":");
            if (ws.length != 2) {
                return;
            }
            String host = ws[0];
            String port = ws[1];
            Observable.create((ObservableOnSubscribe<String>) emitter -> {
                ArrayList<Integer> prots = PortScan.onAddress(host).setMethodTCP().setPort(Integer.valueOf(port)).doScan();
                if (prots != null && prots.size() == 1) {
                    Ping ping = Ping.onAddress(host);
                    ping.setTimeOutMillis(1000);
                    ping.setTimes(5);
                    ping.doPing(new Ping.PingListener() {
                        @Override
                        public void onResult(PingResult pingResult) {
                        }

                        @Override
                        public void onFinished(PingStats pingStats) {
                            String ping = String.format("%.2f", pingStats.getAverageTimeTaken());
                            emitter.onNext(ping);
                            emitter.onComplete();
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
                } else {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Throwable());
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }
                @Override
                public void onNext(String ping) {
                    holder.mTvNodePing.setText(ping + "ms");
                    BigDecimal pingBig = new BigDecimal(ping);
                    if (pingBig.compareTo(PING_QUICK) == -1) {
                        holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_quick));
                    } else if (pingBig.compareTo(PING_LOW) == -1) {
                        holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_normal));
                    } else {
                        holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                    }
                    holder.mTvNodePing.setVisibility(View.VISIBLE);
                    holder.mImgLoad.setVisibility(View.GONE);
                    holder.mProgressDrawable.stop();
                }
                @Override
                public void onError(Throwable e) {
                    holder.mLayoutItem.setClickable(false);
                    holder.mTvNodePing.setText("---");
                    holder.mTvNodePing.setTextColor(getResources().getColor(R.color.color_ping_low));
                    holder.mTvNodePing.setVisibility(View.VISIBLE);
                    holder.mImgLoad.setVisibility(View.GONE);
                    holder.mProgressDrawable.stop();
                }

                @Override
                public void onComplete() {

                }
            });
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return publicNodes.getLength();
        }
    }

    /**
     * 从配置文件读取 默认节点列表
     */
    private void getPublicNode() {
        if(publicNodes.toString().equals("{}")){
            publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
            ConfirmNodeListLength = publicNodes.getLength();
        } else {
            publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
        }
        getCustomNode();
    }
    /**
     * 从本地读取用户节点列表
     */
    private void getCustomNode() {
        String fileName = NodeSettingActivity.this.getPackageName() + "_customNode";
        SharedPreferences sharedPreferences = NodeSettingActivity.this.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String nodes = sharedPreferences.getString("nodes", "");
        List<String> nodeList = new ArrayList<>();;
        if (!TextUtils.isEmpty(nodes)) {
            if (nodes.contains(",")) {
                List<String> arrList = Arrays.asList(nodes.split(","));
                nodeList = new ArrayList(arrList);
            } else {
                nodeList = new ArrayList();
                nodeList.add(nodes);
            }
        }
        publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
        for(int i = 0; i<nodeList.size();i++ ) {
            GsonUtil NewCustomNode = new GsonUtil("{}");
            NewCustomNode.putString("name",Constant.CustomNodeName);
            NewCustomNode.putString("node", nodeList.get(i));
            publicNodes.add(NewCustomNode);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除节点
     */
    private void DeleteNode(int index) {
        Log.d(TAG, "DeleteNode: ConfirmNodeListLength" +ConfirmNodeListLength);
        if (index < ConfirmNodeListLength) {
            ToastUtil.toast(NodeSettingActivity.this, "默认节点不可删除！");
            return;
        } else {
            //从sp删除节点
            GsonUtil item = publicNodes.getObject(index);
            String node = item.getString("node","");
            Log.d(TAG, "DeleteNode: node "+node);
            String fileName = getContext().getPackageName() + "_customNode";
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String nodes = sharedPreferences.getString("nodes", "");
            List<String> nodeList = null;
            if (!TextUtils.isEmpty(nodes)) {
                if (nodes.contains(",")) {
                    List<String> arrList = Arrays.asList(nodes.split(","));
                    nodeList = new ArrayList(arrList);
                } else {
                    nodeList = new ArrayList();
                    nodeList.add(nodes);
                }
            }
            nodeList.remove(node);
            editor.putString("nodes", nodeList.toString().replace("[", "").replace("]", "").replace(" ", ""));
            editor.apply();
            publicNodes.remove(index);
            mAdapter.notifyDataSetChanged();
        }
    }
}
