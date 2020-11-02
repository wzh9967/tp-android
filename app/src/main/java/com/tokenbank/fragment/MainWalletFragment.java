
package com.tokenbank.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.moac.MoacWallet;
import com.android.jccdex.app.util.JCCJson;
import com.tokenbank.R;
import com.tokenbank.activity.TokenDetailsActivity;
import com.tokenbank.activity.TokenReceiveActivity;
import com.tokenbank.activity.TokenTransferActivity;
import com.tokenbank.adapter.BaseRecycleAdapter;
import com.tokenbank.adapter.BaseRecyclerViewHolder;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.WalletActionPop;
import com.tokenbank.dialog.WalletMenuPop;
import com.tokenbank.utils.DefaultItemDecoration;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.NetUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.TokenImageLoader;
import com.tokenbank.utils.ViewUtil;
import com.zxing.activity.CaptureActivity;

import java.util.function.LongFunction;


public class MainWalletFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        BaseRecycleAdapter.OnDataLodingFinish {

    private final static int SCAN_REQUEST_CODE = 10001;
    private Context context;
    private MoacWallet mMoacWallet;
    private SwipeRefreshLayout mSwipteRefreshLayout;
    private AppBarLayout mAppbarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecycleView;
    private MainTokenRecycleViewAdapter mAdapter;
    private View mEmptyView;
    private View mWalletAction, mMenuAction;
    private TextView mTvWalletName, mTvWalletUnit;

    private WalletMenuPop walletMenuPop;
    private WalletActionPop walletActionPop;
    private boolean isAssetVisible = false;
    private BaseWalletUtil mWalletUtil;
    private GsonUtil currency = new GsonUtil("{}");
    private String unit = "¥";
    private boolean isViewCreated = false;
    private double mTotalAsset = 0.0f;

    public static MainWalletFragment newInstance() {
        MainWalletFragment fragment = new MainWalletFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return ViewUtil.inflatView(inflater, container, R.layout.fragment_main_wallet_new, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        mWalletUtil = TBController.getInstance().getWalletUtil();
        isAssetVisible = FileUtil.getBooleanFromSp(getContext(), Constant.common_prefs, Constant.asset_visible_key, true);
        mSwipteRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
        mSwipteRefreshLayout.setOnRefreshListener(this);

        mAppbarLayout = view.findViewById(R.id.main_appbar);
        mAppbarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);

        mToolbar = view.findViewById(R.id.toolbar);
        mWalletAction = view.findViewById(R.id.wallet_menu_action);
        mTvWalletName = view.findViewById(R.id.tv_wallet_name);
        setWalletName();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mEmptyView = view.findViewById(R.id.empty_view);

        //我的资产
        mTvWalletUnit = view.findViewById(R.id.wallet_unit);
        mTvWalletUnit.setOnClickListener(this);
        mRecycleView = view.findViewById(R.id.mainwallet_recycleview);
        mRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isReadyForPullEnd()) {
                    //最后一个可见
                    mAdapter.loadmore(null);
                }
            }
        });
        mAdapter = new MainTokenRecycleViewAdapter();
        mAdapter.setDataLoadingListener(this);
        mRecycleView.addItemDecoration(
                new DefaultItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_line)));
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter);

        mTvWalletName.setOnClickListener(this);
        mMenuAction = view.findViewById(R.id.wallet_menu);
        mMenuAction.setOnClickListener(this);

        view.findViewById(R.id.wallet_action_receive1).setOnClickListener(this);
        view.findViewById(R.id.wallet_action_receive).setOnClickListener(this);
        view.findViewById(R.id.wallet_action_transfer).setOnClickListener(this);
        view.findViewById(R.id.wallet_action_transfer1).setOnClickListener(this);
        isViewCreated = true;
        this.context = getActivity().getApplicationContext();
        mMoacWallet =TBController.getInstance().getMoacWallet();
        currency =new GsonUtil(FileUtil.getConfigFile(this.context, "currency.json"));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshWallet();
    }

    @Override
    public void onClick(View view) {
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            ToastUtil.toast(getContext(), getString(R.string.toast_no_network));
            return;
        }
        switch (view.getId()) {
            case R.id.tv_wallet_name:
                showWalletMenuPop();
                break;
            case R.id.wallet_menu:
                showActionMenuPop();
                break;
            case R.id.wallet_unit:
                setAssetVisible();
                break;
            case R.id.wallet_action_transfer:
            case R.id.wallet_action_transfer1:
                TokenTransferActivity.startTokenTransferActivity(getContext(), "", "", "",
                        "",mWalletUtil.getDefaultTokenSymbol(), mWalletUtil.getDefaultDecimal(), 0);
                break;
            case R.id.wallet_action_receive:
            case R.id.wallet_action_receive1:
                TokenReceiveActivity.startTokenReceiveActivity(getActivity(), mWalletUtil.getDefaultTokenSymbol());
                break;
        }
    }

    /**
     * 显示钱包菜单pop
     */
    private void showWalletMenuPop() {
        walletMenuPop = new WalletMenuPop(getActivity());
        walletMenuPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mTvWalletName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0,
                        R.drawable.ic_arrow_down, 0);
                refreshWallet();
            }
        });
        walletMenuPop.setData();
        walletMenuPop.showAsDropDown(mTvWalletName);
        mTvWalletName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, R.drawable.ic_arrow_up, 0);
    }

    private void refreshWallet() {
        setWalletName();
        refresh();
        mWalletUtil = TBController.getInstance().getWalletUtil();
    }

    /**
     * 显示功能菜单pop
     */
    private void showActionMenuPop() {
        if (walletActionPop == null) {
            walletActionPop = new WalletActionPop(getActivity(), new WalletActionPop.ScanClickListener() {
                @Override
                public void onScanClick() {
                    startActivityForResult(new Intent(getActivity(), CaptureActivity.class), SCAN_REQUEST_CODE);

                }
            });
        }
        walletActionPop.showAsDropDown(mMenuAction);
    }

    private void setAssetVisible() {
        if (mAdapter != null && mAdapter.getLength() > 0) {
            isAssetVisible = !isAssetVisible;
            FileUtil.putBooleanToSp(getContext(), Constant.common_prefs, Constant.asset_visible_key, isAssetVisible);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        mAdapter.refresh();
    }

    @Override
    public <K> void onDataLoadingFinish(K params, boolean end, boolean loadmore) {
        if (!loadmore) {
            if (end) {
                if (mAdapter.getLength() <= 0) {
                    mRecycleView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecycleView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void update() {
        mTvWalletUnit.setText(String.format(getString(R.string.content_my_asset)));
        setWalletName();
    }

    private void setWalletName() {
        mTvWalletName.setText(WalletInfoManager.getInstance().getWname());
    }

    private void refresh() {
        mAdapter.refresh();
        mSwipteRefreshLayout.setRefreshing(true);
    }

    private boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRecycleView.getChildAdapterPosition(
                    mRecycleView.getChildAt(mRecycleView.getChildCount() - 1));
            if (lastVisiblePosition >= mRecycleView.getAdapter().getItemCount() - 1) {
                return mRecycleView.getChildAt(mRecycleView.getChildCount() - 1).getBottom()
                        <= mRecycleView.getBottom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
            mToolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.colorPrimary), fraction));
            if (fraction < 0.5f) {
                if (mTvWalletName.getVisibility() != View.VISIBLE) {
                    mTvWalletName.setVisibility(View.VISIBLE);
                    mWalletAction.setVisibility(View.GONE);
                }
                if (fraction < 0.3) {
                    mTvWalletName.setAlpha(1);
                } else {
                    mTvWalletName.setAlpha(1 - (float) ((fraction - 0.3) * 5));
                }
            } else {
                if (mWalletAction.getVisibility() != View.VISIBLE) {
                    mTvWalletName.setVisibility(View.GONE);
                    mWalletAction.setVisibility(View.VISIBLE);
                }
                if (fraction > 0.7) {
                    mWalletAction.setAlpha(1);
                } else {
                    mWalletAction.setAlpha((float) ((fraction - 0.5) * 5));
                }
            }
            if (verticalOffset >= 0) {
                if (!mSwipteRefreshLayout.isEnabled()) {
                    mSwipteRefreshLayout.setEnabled(true);
                }
            } else {
                if (mSwipteRefreshLayout.isEnabled()) {
                    mSwipteRefreshLayout.setEnabled(false);
                }
            }
        }
    };

    /**
     * 根据百分比改变颜色透明度
     */
    private int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    class MainTokenRecycleViewAdapter extends BaseRecycleAdapter<String, RecyclerView.ViewHolder> {

        private static final String TAG = "MainTokenAdapter";
        private boolean mHasMore = true;
        private int mPageIndex = 0;
        private final static int PAGE_SIZE = 10;

        //监听点击事件跳到对应交易细节
        private BaseRecyclerViewHolder.ItemClickListener mItemClickListener =
                new BaseRecyclerViewHolder.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gotoTokenDetail(MainTokenRecycleViewAdapter.this.getItem(position));
                    }
                };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ViewUtil.inflatView(getContext(), parent, R.layout.wallet_token_item_view, false);
            return new TokenViewHolder(view, mItemClickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GsonUtil itemData = getItem(position);
            getBalance(itemData,(TokenViewHolder) holder);
        }

        //加载数据
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
            mMoacWallet.gasPrice(new JCallback() {
                @Override
                public void completion(JCCJson jccJson) {
                    handleTokenRequestResult(params, loadmore, currency);
                    mSwipteRefreshLayout.setRefreshing(false);
                }
            });
        }

        private void handleTokenRequestResult(final String params, final boolean loadmore, GsonUtil json) {
            GsonUtil data = json.getObject("data", "{}");
            unit = data.getString("unit", "¥");
            GsonUtil tokens = json.getArray("data", "");
            if (!loadmore) {
                //第一页
                setData(tokens);
            } else {
                if (tokens.getLength() > 0) {
                    addData(tokens);
                }
            }
            if (!loadmore) {
                update();
            }
            mHasMore = false;
            if (mDataLoadingListener != null) {
                mDataLoadingListener.onDataLoadingFinish(params, true, loadmore);
            }
        }

        private void fillTokenData(TokenViewHolder holder, GsonUtil data) {
            TokenImageLoader.displayImage(data.getString("icon_url", ""), holder.mImgTokenIcon,
                    TokenImageLoader.imageOption(R.drawable.ic_images_common_loading, R.drawable.ic_images_asset_eth,
                            R.drawable.ic_images_asset_eth));
            holder.mTvTokenName.setText(data.getString("bl_symbol", "MOC"));
            if (isAssetVisible) {
                String value = mWalletUtil.toValue(data.getInt("decimal", 0), data.getString("balance",""));
                holder.mTvTokenCount.setText("" +value);
            } else {
                holder.mTvTokenCount.setText("***");
            }
        }

        private void getBalance(GsonUtil data,TokenViewHolder holder){
            String contract = data.getString("contract","");
            String address = WalletInfoManager.getInstance().getWAddress();
            if(contract.equals("")){
                mWalletUtil.getBalance(address,new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if(ret == 0){
                            data.putString("balance",extra.getString("balance",""));
                            Log.d("getBalance","balance = "+extra.getString("balance",""));
                        }
                        fillTokenData((TokenViewHolder) holder, data);
                    }
                });
            } else {
                mWalletUtil.getErc20Balance(contract,address,new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if(ret == 0){
                            data.putString("balance",extra.getString("balance",""));
                            Log.d("getBalance","balance = "+extra.getString("balance",""));
                        }
                        fillTokenData((TokenViewHolder) holder, data);
                    }
                });
            }
        }
        private void gotoTokenDetail(GsonUtil data) {
            TokenDetailsActivity.NavToActivity(getActivity(), data.toString(), unit);
        }

        class TokenViewHolder extends BaseRecyclerViewHolder {

            ImageView mImgTokenIcon;
            TextView mTvTokenName;
            TextView mTvTokenCount;
            TextView mTvTokenAsset;

            public TokenViewHolder(View itemView, ItemClickListener onItemClickListener) {
                super(itemView, onItemClickListener);
                mImgTokenIcon = itemView.findViewById(R.id.token_icon);
                mTvTokenName = itemView.findViewById(R.id.token_name);
                mTvTokenCount = itemView.findViewById(R.id.token_count);
                mTvTokenAsset = itemView.findViewById(R.id.token_asset);
            }
        }
    }
}