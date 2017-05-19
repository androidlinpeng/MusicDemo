package msgcopy.com.musicdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.OnPlayerListener;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.utils.NetworkUtil;
import msgcopy.com.musicdemo.utils.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity implements OnPlayerListener{

    private BaseBroadcastReceiver baseBroadcastReceiver;

    public MyApplication getAppContext() {
        return (MyApplication) getApplication();
    }

    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, true);
    }

    public void startActivity(Intent intent, boolean anim) {
        super.startActivity(intent);
        if (anim) {
//            overridePendingTransition(R.anim.push_left_in, R.anim.nothing);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, true);
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean anim) {
        super.startActivityForResult(intent, requestCode);
        if (anim) {
//            overridePendingTransition(R.anim.push_left_in, R.anim.nothing);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        baseBroadcastReceiver = new BaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Constants.MUSIC_LISTENER);
        registerReceiver(baseBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(baseBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.push_right_out);
    }

    @Override
    public void OnChangedSong(Song song) {

    }

    @Override
    public void onChengedProgress(PlayState playState) {

    }

    private class BaseBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                        ToastUtils.showLong(getApplication(), R.string.str_network_unconnect);
                    } else {
                        int type = NetworkUtil.getAPNType(getAppContext());
                        if (type == 1) {
                            String net = getString(R.string.str_network_connect, "net网络");
//                            ToastUtils.showLong(getApplication(), net);
                        } else if (type == 2) {
                            String net = getString(R.string.str_network_connect, "wap网络");
//                            ToastUtils.showLong(getApplication(), net);
                        } else if (type == 3) {
                            String net = getString(R.string.str_network_connect, "WIFI网络");
//                            ToastUtils.showLong(getApplication(), net);
                        }
                    }
                }else if (action.equals(Constants.MUSIC_LISTENER)){
                    MyApplication.getInstance().getMusicService().setOnPlayerListener(BaseActivity.this);
                }
            }
        }
    };
}
