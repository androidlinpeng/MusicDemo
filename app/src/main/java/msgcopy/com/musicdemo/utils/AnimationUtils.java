package msgcopy.com.musicdemo.utils;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * Created by liang on 2017/5/19.
 */

public class AnimationUtils {

    public static void showAnimationHint(final TextView textView){
        textView.setVisibility(View.VISIBLE);
        textView.setAnimation(moveToViewLocation());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.GONE);
                textView.setAnimation(moveToViewTop
                        ());
            }
        },1500);
    }

    public static TranslateAnimation moveToViewTop() {
        TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        hiddenAction.setDuration(500);
        return hiddenAction;
    }

    public static TranslateAnimation moveToViewLocation() {
        TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        showAction.setDuration(500);
        return showAction;
    }
}
