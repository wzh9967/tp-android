package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.adapter.BaseRecycleAdapter;
import com.tokenbank.adapter.BaseRecyclerViewHolder;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.config.Constant;
import com.tokenbank.net.query.QueryDataFromNet;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;
import com.tokenbank.wallet.WalletInfoManager;

/**
 * 币种详情页，包含原生货币和Erc20币 价值和对应币种交易记录
 */
public class TokenDetailsActivity extends BaseActivity implements BaseRecycleAdapter.OnDataLodingFinish, View.OnClickListener {

    private static final String TAG = "TokenDetailsActivity";
    private static final String TOKEN = "Token";
    private static final String UNIT_KEY = "Unit_Key";

    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;
    private TokenDetailsActivity.RecyclerViewAdapter mAdapter;
    private View mEmptyView;

    private LinearLayout mLayoutTranster;
    private LinearLayout mLayoutReceive;

    private GsonUtil mItem;
    private String mValue;
    private int mDecimal;
    private String bl_symbol;
    private String TokenName;
    private String mContractAddress;
    private WalletInfoManager.WData mWalletData;
    private String mUnit;
    private boolean Flag = true;
    private String maddress;
    private int PageSize = 1;
    private QueryDataFromNet mQueryTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_details_activity);
        initData();
        initView();
    }

    private void initData() {
        maddress =  WalletInfoManager.getInstance().getWAddress();
        if (getIntent() != null) {
            //erc20币种数据json
            mItem = new GsonUtil(getIntent().getStringExtra(TOKEN));
        }
        mWalletData = WalletInfoManager.getInstance().getCurrentWallet();
        mQueryTransaction = TBController.getInstance().getmQueryTransaction();
        if (mWalletData == null) {
            this.finish();
            return;
        }
        mContractAddress = mItem.getString("contract", "");
        mDecimal = mItem.getInt("decimal", Constant.DefaultDecimal);
        bl_symbol = mItem.getString("bl_symbol", "");
        TokenName = mItem.getString("name", "");

    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mLayoutTranster) {

            TokenTransferActivity.startTokenTransferActivity(TokenDetailsActivity.this, "",
                    mContractAddress, mValue, TokenName,bl_symbol, mDecimal, "");
        } else if (v == mLayoutReceive) {
            TokenReceiveActivity.startTokenReceiveActivity(TokenDetailsActivity.this, mContractAddress,bl_symbol);
        }
    }

    public static void NavToActivity(Context context, String item, String unit) {
        Intent intent = new Intent(context, TokenDetailsActivity.class);
        intent.putExtra(TOKEN, item);
        intent.putExtra(UNIT_KEY, unit);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                TokenDetailsActivity.this.finish();
            }
        });
        mTitleBar.setTitle(bl_symbol);
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new RecyclerViewAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TokenDetailsActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });

        mLayoutTranster = findViewById(R.id.wallet_action_transfer);
        mLayoutTranster.setOnClickListener(this);
        mLayoutReceive = findViewById(R.id.wallet_action_receive);
        mLayoutReceive.setOnClickListener(this);
        TextView tvBalance = findViewById(R.id.token_balance);
        TextView tvAsset = findViewById(R.id.token_asset);


        //显示余额和估值金额
        mUnit = getIntent().getStringExtra(UNIT_KEY);
        if (TextUtils.isEmpty(mUnit)) {
            mUnit = "$";
        }
        //显示余额
        String balance = mItem.getString("balance", "0");
        if(balance.equals("***")){
            mValue = "***";
        } else {
            mValue = Util.toValue(mDecimal, balance);
        }
        tvBalance.setText(mValue);

        //转换为等值人民币
        tvAsset.setText(String.format("≈ %1s %2s", mUnit, Util.formatDoubleToStr(2, Util.strToDouble(
                mItem.getString("asset", "0")))));

        mAdapter.refresh();
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecyclerView.getChildAdapterPosition(
                    mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecyclerView.getAdapter().getItemCount() - 1) {
                return mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1)
                        .getBottom() <= mRecyclerView.getBottom();
            }
        } catch (Throwable e) {
        }
        return false;
    }

    class RecyclerViewAdapter extends BaseRecycleAdapter<String, RecyclerViewAdapter.ViewHolder> {

        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;

        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener = new BaseRecyclerViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GsonUtil item = getItem(position);
                gotoTransactionDetail(item.getString("transactionHash",""));
            }
        };

        @Override
        public void loadData(final String params, final boolean loadmore) {
            if (!loadmore) {
                mPageIndex = 0;
            } else {
                mPageIndex++;
            }
            if (loadmore && !mHasMore) {
                return;
            }
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, false, loadmore);
            }
            if(mContractAddress.equals("")){
                Flag = true;
                mQueryTransaction.queryTransactionList(PageSize,maddress,new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if (ret == 0) {
                            GsonUtil moabTransactionRecord = extra.getArray("moabData", "[]");
                            handleTransactioRecordResult(params, loadmore, moabTransactionRecord);
                            Retry(params,loadmore);
                        }
                    }
                });
            } else {
                Flag = false;
                mQueryTransaction.queryErc20TransactionList(PageSize,mDecimal,mContractAddress, maddress,new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if (ret == 0) {
                            GsonUtil Erc20TransactionRecord = extra.getArray("data", "[]");
                            handleTransactioRecordResult(params, loadmore, Erc20TransactionRecord);
                            Retry(params,loadmore);
                        }
                    }
                });
            }
        }
        public void Retry(String params,  boolean loadmore){
            if(PageSize == 1){
                loadmore = true;
            }
            PageSize++;
            if(PageSize > 10){
                PageSize =1;
                return;
            }
            loadData(params,loadmore);
            return;
        }
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewAdapter.ViewHolder holder = new RecyclerViewAdapter.ViewHolder(ViewUtil.inflatView(parent.getContext(),
                    parent, R.layout.layout_item_transaction, false), mItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position) {
            fillData(viewHolder, getItem(position));
        }

        private void handleTransactioRecordResult(final String params, final boolean loadmore, GsonUtil transactionRecord) {
            if (!loadmore) {
                //第一页
                setData(transactionRecord);
            } else {
                if (transactionRecord.getLength() > 0) {
                    addData(transactionRecord);
                }
            }
            if (transactionRecord.getLength() < PAGE_SIZE) {
                //最后一页了
                mHasMore = false;
            } else {
                mHasMore = true;
            }
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillData(final ViewHolder holder, final GsonUtil item) {

            if (item == null || TextUtils.equals(item.toString(), "{}")) {
                return;
            }
            String toAddress = item.getString("to", "");
            String fromAddress = item.getString("from", "");
            String currentAddress = WalletInfoManager.getInstance().getWAddress().toLowerCase();
            String value = item.getString("value", "");;
            boolean in = false;
            holder.mTvTransactionTime.setText(item.getString("timestamp", ""));
            String label = "";
            if (TextUtils.equals(currentAddress, fromAddress)) {
                label = "-";
                in = false;
            }
            if (TextUtils.equals(currentAddress, toAddress)) {
                label = "+";
                in = true;
            }
            if (in) {
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_in);
                holder.mTvTransactionAddress.setText(fromAddress);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_blue));
            } else {
                holder.mTvTransactionAddress.setText(toAddress);
                holder.mImgIcon.setImageResource(R.drawable.ic_transaction_out);
                holder.mTvTransactionCount.setTextColor(getResources().getColor(R.color.common_red));
            }
            holder.mTvTransactionCount.setText(label + value);
        }

        private void gotoTransactionDetail(String hash) {
            TransactionDetailsActivity.startTransactionDetailActivity(TokenDetailsActivity.this, hash,false);
        }
        class ViewHolder extends BaseRecyclerViewHolder {
            ImageView mImgIcon;
            TextView mTvTransactionAddress;
            TextView mTvTransactionTime;
            TextView mTvTransactionCount;

            public ViewHolder(View itemView, ItemClickListener itemClickListener) {
                super(itemView, itemClickListener);
                mImgIcon = itemView.findViewById(R.id.img_icon);
                mTvTransactionAddress = itemView.findViewById(R.id.tv_transaction_address);
                mTvTransactionTime = itemView.findViewById(R.id.tv_transaction_time);
                mTvTransactionCount = itemView.findViewById(R.id.tv_transaction_count);
            }
        }
    }
}
