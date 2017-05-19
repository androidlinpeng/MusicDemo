package msgcopy.com.musicdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.OnPlayerListener;
import msgcopy.com.musicdemo.utils.MsgCache;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.BitmapUtils;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.view.BlurringView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LockScreenActivity extends SwipeBackActivity implements OnPlayerListener{

    private static final String TAG = "LockScreenActivity";

    @BindView(R.id.lock_screen_play)
    ImageView lockscreenplay;
    @BindView(R.id.lock_screen_last)
    ImageView lockscreenlast;
    @BindView(R.id.lock_screen_next)
    ImageView lockscreennext;
    @BindView(R.id.lock_screen_title)
    TextView lockscreentitle;
    @BindView(R.id.lock_screen_subtitle)
    TextView lockscreensubtitle;
    @BindView(R.id.lock_screen_subtitle_2)
    TextView lockscreensubtitle_2;


    @BindView(R.id.blur_view)
    ImageView blur_view;
    @BindView(R.id.blurring_view)
    BlurringView blurring_view;
    @BindView(R.id.civ_test)
    ImageView civ_test;


    private Song currentsong;
    private boolean isPlaying = false;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //越过手机锁屏界面
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        try {
            currentsong = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);

        if (!CommonUtil.isBlank(currentsong)) {
            initView(currentsong);
        }
        subscribeChangedSong();
//        subscribePlayState();
        MyApplication.getInstance().getMusicService().setOnPlayerListener(this);

    }

    private void initView(Song song) {
        Bitmap bitmap = BitmapUtils.createAlbumArt(song.path);
        lockscreentitle.setText(song.title);
        lockscreensubtitle.setText(song.artistName);
        lockscreensubtitle_2.setText(song.albumName);
        blurring_view.setBlurRadius(8);
        blurring_view.setDownsampleFactor(8);
        if (CommonUtil.isBlank(bitmap)) {
            blur_view.setImageResource(R.drawable.icon_album_dark);
            civ_test.setImageResource(R.drawable.icon_album_dark);
        } else {
            blur_view.setImageBitmap(bitmap);
            civ_test.setImageBitmap(bitmap);
        }
        blur_view.setBackgroundResource(R.color.transparent);
        blurring_view.setBlurredView(blur_view);
        blurring_view.invalidate();
        currentsong = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
    }

    private void swipeGestures() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // e1: 第一次按下的位置   e2:  当手离开屏幕 时的位置  velocityX:  沿x 轴的速度  velocityY： 沿Y轴方向的速度
                //向上滑动关闭
                if ((e1.getRawY() - e2.getRawY() ) > 100){
                    finish();
                    return true;//消费掉当前事件
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }


    @OnClick({R.id.lock_screen_play,R.id.lock_screen_last,R.id.lock_screen_next})
    public void LockScreen(ImageView imag){
        switch (imag.getId()){
            case R.id.lock_screen_last:
                if (!CommonUtil.isBlank(currentsong)) {
                    this.lockscreenplay.setImageResource(R.drawable.ic_play_white_36dp);
                    MyApplication.getInstance().getMusicService().last();
                }
                break;
            case R.id.lock_screen_play:
                if (!CommonUtil.isBlank(currentsong)) {
                    MyApplication.getInstance().getMusicService().play();
                    if (!CommonUtil.isBlank(currentsong.path)) {
                        if (isPlaying) {
                            isPlaying = false;
                        } else {
                            isPlaying = true;
                        }
                    }
                    updatePausePlay(isPlaying);
                }
                break;
            case R.id.lock_screen_next:
                if (!CommonUtil.isBlank(currentsong)) {
                    this.lockscreenplay.setImageResource(R.drawable.ic_play_white_36dp);
                    MyApplication.getInstance().getMusicService().next();
                }
                break;
        }
    }

    private void subscribeChangedSong() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(Song.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .distinctUntilChanged()
                .subscribe(new Action1<Song>() {
                    @Override
                    public void call(Song song) {
                        initView(song);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

//    private void subscribePlayState() {
//        Subscription subscription = RxBus.getInstance()
//                .toObservable(PlayState.class)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
////                .distinctUntilChanged()
//                .subscribe(new Action1<PlayState>() {
//                    @Override
//                    public void call(PlayState playState) {
//                        isPlaying = playState.isPlaying();
//                        updatePausePlay(isPlaying);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                    }
//                });
//        RxBus.getInstance().addSubscription(this, subscription);
//    }

    private void updatePausePlay(boolean isPlaying) {
        if (isPlaying) {
            this.lockscreenplay.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            this.lockscreenplay.setImageResource(R.drawable.ic_play_white_36dp);
        }
    }
    private void sendService(int status) {
        Intent intentService = new Intent(LockScreenActivity.this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", currentsong.path);
        bundle.putInt("status", status);
        bundle.putLong("songID", currentsong.id);
        intentService.putExtra("bundle", bundle);
        startService(intentService);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void OnChangedSong(Song song) {

    }

    @Override
    public void onChengedProgress(PlayState playState) {
        isPlaying = playState.isPlaying();
        updatePausePlay(isPlaying);
    }
}
