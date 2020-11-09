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
import com.tokenbank.base.FstServer;
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
 * 节点设置
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
                        getCustomNode();
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onLeftClick(View view) {
        saveNode();
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
                            GsonUtil item = publicNodes.getObject(mSelectedItem);
                            item.putInt("position",mSelectedItem);
                            FstServer.getInstance().setNode(item);
                            vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                            vh.mRadioSelected.setChecked(true);
                            vh.mLayoutItem.setActivated(true);
                        } else {
                            //vh == null
                            if (mSelectedItem != -1) {
                                notifyItemChanged(mSelectedItem);
                            }
                            mSelectedItem = position;
                            GsonUtil item = publicNodes.getObject(mSelectedItem);
                            item.putInt("position",mSelectedItem);
                            FstServer.getInstance().setNode(item);
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
            if(position == 0 && FstServer.getInstance().getIndex() == -1){
                item.putInt("position",position);
                FstServer.getInstance().setNode(item);
                holder.mRadioSelected.setChecked(true);
                holder.mLayoutItem.setActivated(true);
                mSelectedItem = position;
            }
            if(FstServer.getInstance().getIndex() != -1 && FstServer.getInstance().getIndex() == position){
                holder.mRadioSelected.setChecked(true);
                holder.mLayoutItem.setActivated(true);
                mSelectedItem = position;
            }
            holder.mTvNodeName.setText(item.getString("name", ""));
            String url = item.getString("node", "");
            holder.mTvNodeUrl.setText(url);

            holder.mProgressDrawable.start();
            String[] ws = url.replace("http://", "").replace("https://", "").split(":");
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
        publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
        if(publicNodes.toString().equals("{}")){

            ConfirmNodeListLength = publicNodes.getLength();
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
     * 保存节点
     */
    private void saveNode() {
        String url;
        String ping = null;
        if (mSelectedItem != -1) {
            NodeRecordAdapter.VH vh = (NodeRecordAdapter.VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
            url = vh.mTvNodeUrl.getText().toString();
            ping = vh.mTvNodePing.getText().toString();
        } else {
            return;
        }
        ping = ping.replace("ms", "");
        if (!TextUtils.isEmpty(ping) && !TextUtils.equals(ping, "---")) {
            GsonUtil item = publicNodes.getObject(mSelectedItem);
            item.putInt("position",mSelectedItem);
            FstServer.getInstance().setNode(item);
        }
    }

    /**
     * 删除节点
     */
    private void DeleteNode(int index) {
        if (index < ConfirmNodeListLength) {
            ToastUtil.toast(NodeSettingActivity.this, "默认节点不可删除！");
            return;
        } else {
            //从sp删除节点
            GsonUtil item = publicNodes.getObject(index);
            String node = item.getString("node","");
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
