package msgcopy.com.musicdemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;

public class BaseActivity extends AppCompatActivity {

    public MyApplication getAppContext(){
        return (MyApplication)getApplication();
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

    public void startActivity(Intent intent, boolean anim){
        super.startActivity(intent);
        if (anim){
//            overridePendingTransition(R.anim.push_left_in, R.anim.nothing);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, true);
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean anim) {
        super.startActivityForResult(intent, requestCode);
        if (anim){
//            overridePendingTransition(R.anim.push_left_in, R.anim.nothing);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //透明状态栏
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(BASE_INTENT_ACTION_FINISH);
//        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(baseBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(baseBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing,R.anim.push_right_out);
    }


//    public static final String BASE_INTENT_ACTION_FINISH="base_intent_action_finish";
//
//    private BroadcastReceiver baseBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (null!=intent){
//                String action=intent.getAction();
//                if (action.equals(BASE_INTENT_ACTION_FINISH)){
//                    finish();
//                }
//            }
//        }
//    };
}
