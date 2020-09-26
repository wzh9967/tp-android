package com.tokenbank.activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.tokenbank.R;
import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.NodeCustomDialog;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化：
 * 1, 默认节点从本地获取         读取json
 * 2, 初始化存在sp中
 * 3, 如果不为空不重新加，载以后ping测试
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
    private int mSelectedCustomItem = -1;
    private List<String> publicNodesCustom = new ArrayList<>();
    private GsonUtil publicNodes = new GsonUtil("{}");
    private NodeRecordAdapter mAdapter;
    private Button mBtnAddNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_node);
        initView();
    }

    private void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_node_setting :
                new NodeCustomDialog(NodeSettingActivity.this, new NodeCustomDialog.onConfirmOrderListener() {
                    @Override
                    public void onConfirmOrder() {
                        mSelectedCustomItem = 0;
                    }
                }).show();
                break;
        }

    }

    @Override
    public void onLeftClick(View view) {

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

    //自定义一个适配器来进行创建item view以及绑定数据
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
            public VH(View itemView) {
                super(itemView);
                //设置栏目样式
                mLayoutItem = itemView.findViewById(R.id.layout_item);
                mTvNodeUrl = itemView.findViewById(R.id.tv_node_url);
                mTvNodeName = itemView.findViewById(R.id.tv_node_name);
                mTvNodePing = itemView.findViewById(R.id.tv_ping);
                mImgLoad = itemView.findViewById(R.id.img_ping);
                mRadioSelected = itemView.findViewById(R.id.radio_selected);
                mRadioSelected.setClickable(false);
                mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VH vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                        int position = getAdapterPosition();
                        if (position == mSelectedItem) {
                            return;
                        } else if (position != mSelectedItem && vh != null) {
                            vh.mRadioSelected.setChecked(false);
                            vh.mLayoutItem.setActivated(false);
                            mSelectedItem = position;
                            vh = (VH) mNodeRecyclerView.findViewHolderForLayoutPosition(mSelectedItem);
                            vh.mRadioSelected.setChecked(true);
                            vh.mLayoutItem.setActivated(true);
                        } else {
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
            }
        }

        /**
         * 数据的绑定显示
         *创建 viewholder,引入页面的xml传送给viewholder
         */
        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_node, false);
            return new VH(v);
        }

        /**
         * 操作item
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(VH holder, @SuppressLint("RecyclerView") int position) {
            if (publicNodes == null || publicNodes.getLength() == 0) {
                return;
            }
            GsonUtil item = publicNodes.getObject(position);
            Log.d(TAG, "onBindViewHolder: item"+item.toString());
            holder.mTvNodeName.setText(item.getString("name", ""));
            String url = item.getString("node", "");
            holder.mTvNodeUrl.setText(url);
            holder.mLayoutItem.setClickable(true);
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
    private void getPublicNode() {
        if (validWalletData(Constant.wallet_def_file)) {

        }
        publicNodes = new GsonUtil(FileUtil.getConfigFile(this, "publicNode.json"));
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean validWalletData(String fileName) {
        if (!TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.NodeName))
                && !TextUtils.isEmpty(FileUtil.getStringFromSp(AppConfig.getContext(), fileName, Constant.NodeUrl))
               ) {
            return true;
        }
        return false;
    }
}
