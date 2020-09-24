package com.tokenbank.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.tokenbank.R;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.view.TitleBar;

public class ChooseImportWayActivity extends BaseActivity {
    private static final int REQUEST_CODE = 1007;
    public final static String TAG = "ChooseImportWayActivity";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_util);
        initView();
    }
    private void initView() {
        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_select_import_way));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightDrawable(R.drawable.ic_walletutil_help);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightClick(View view) {
                ToastUtil.toast(ChooseImportWayActivity.this, "帮助");
            }
        });

        ListView listView = findViewById(R.id.listview);
        String data[] = {"通过密钥导入","通过助记词导入"};//假数据
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 本地无钱包时，直接返回
                Log.d(TAG, "onItemClick: postion = "+position);
                Intent intent = new Intent();
                switch (position){
                    case 0 :
                    case 1 :
                        intent.putExtra("position", position);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        break;
                }
                finish();
                return;
            }
        });
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void navToActivity(Context context, int requestCode) {
        Log.d(TAG, "navToActivity: 已经跳转到导入方式");
        Intent intent = new Intent(context, ChooseImportWayActivity.class);
        //返回值到context ,结果在onActivityResult中处理  在setResult 传递过去
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
