package msgcopy.com.musicdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.BitmapUtils;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.ToastUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MusicPlayerActivity";

    private final static String SP_ARTICLE_BRIGHT_KEY = "article_bright_display";

    private ImageView blurview = null;

    private TextView music_name = null;

    private TextView artist = null;

    private ImageView play = null;

    private ImageView last = null;

    private ImageView next = null;

    private ImageView single = null;

    private ImageView circulation = null;

    private ImageView random = null;

    private ImageView panel_back = null;

    private ImageView music_share = null;

    private RelativeLayout player_pattern = null;

    private TextView mediaTime = null;

    private TextView curMediaTime = null;

    private CircleImageView civTest;

    private int INTERVAL = 45;//歌词每行的间隔

    private ProgressBar mediaProgress = null;

    private StringBuilder mFormatBuilder;

    private Formatter mFormatter;

    private boolean isPlaying = false;

    private String musicname = null;

    private String musicArtist = null;

    private String musicPath = null;

    private int position;

    private int duration;

    private int pattern ;

    private long songID;

//    private BroadcastReceiver MReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LogUtil.i("onReceive", "onReceive");
//            if (intent != null) {
//                String action = intent.getAction();
//                if (action.equals(MusicService.MUSIC_PLAYER_STATE)) {
//                    Bundle bundle = intent.getExtras();
//                    String path = bundle.getString("currentMusicPath");
//                    position = bundle.getInt("currentTime");
//                    duration = bundle.getInt("mediaTime");
//                    isPlaying = bundle.getBoolean("isPlaying");
//                    play.setImageResource(R.drawable.ic_pause_white_36dp);
//
//                    if (mediaProgress != null) {
//                        if (duration > 0) {
//                            long pos = 1000L * position / duration;
//                            //显示播放进度
//                            mediaProgress.setProgress((int) pos);
//                        }
//                    }
//
//                    if (curMediaTime != null) {
//                        curMediaTime.setText(stringForTime(position));
//                    }
//                    if (mediaTime != null) {
//                        mediaTime.setText(stringForTime(duration));
//                    }
//                    updatePausePlay(isPlaying);
//                    if (!path.equals(musicPath)) {
//                        Song infoEntity = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
//                        musicname = infoEntity.title;
//                        musicArtist = infoEntity.artistName;
//                        musicPath = infoEntity.path;
//                        songID = infoEntity.id;
//                        music_name.setText(musicname);
//                        artist.setText(musicArtist);
//                        initView();
//                    }
//                }
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Song infoEntity = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
            this.musicname = infoEntity.title;
            this.musicArtist = infoEntity.artistName;
            this.musicPath = infoEntity.path;
            this.songID = infoEntity.id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        pattern = getPlayerPattern();

        setContentView(R.layout.activity_music_player);

        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        initMediaController();
        initView();

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicService.MUSIC_PLAYER_STATE);
//        this.registerReceiver(this.MReceiver, intentFilter);

        subscribePlayState();

    }

    private void subscribePlayState() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PlayState.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .distinctUntilChanged()
                .subscribe(new Action1<PlayState>() {
                    @Override
                    public void call(PlayState playState) {
                        if (mediaProgress != null) {
                            String path = playState.getCurrentPath();
                            position = playState.getCurrentTime();
                            duration = playState.getMediaTime();
                            isPlaying = playState.isPlaying();
                            play.setImageResource(R.drawable.ic_pause_white_36dp);

                            if (mediaProgress != null) {
                                if (duration > 0) {
                                    long pos = 1000L * position / duration;
                                    //显示播放进度
                                    mediaProgress.setProgress((int) pos);
                                }
                            }

                            if (curMediaTime != null) {
                                curMediaTime.setText(stringForTime(position));
                            }
                            if (mediaTime != null) {
                                mediaTime.setText(stringForTime(duration));
                            }
                            updatePausePlay(isPlaying);
                            if (!path.equals(musicPath)) {
                                Song infoEntity = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
                                musicname = infoEntity.title;
                                musicArtist = infoEntity.artistName;
                                musicPath = infoEntity.path;
                                songID = infoEntity.id;
                                music_name.setText(musicname);
                                artist.setText(musicArtist);
                                initView();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void initMediaController() {

        this.music_share = (ImageView)findViewById(R.id.music_share);
        this.panel_back = (ImageView)findViewById(R.id.panel_back);
        this.player_pattern = (RelativeLayout)findViewById(R.id.player_pattern);
        this.single = (ImageView) findViewById(R.id.single);
        this.circulation = (ImageView) findViewById(R.id.circulation);
        this.random = (ImageView) findViewById(R.id.random);
        this.music_name = (TextView) findViewById(R.id.music_name);
        this.artist = (TextView) findViewById(R.id.artist);
        this.play = (ImageView) findViewById(R.id.play);
        this.last = (ImageView) findViewById(R.id.last);
        this.next = (ImageView) findViewById(R.id.next);
        this.curMediaTime = (TextView) findViewById(R.id.time_current);
        this.mediaTime = (TextView) findViewById(R.id.time);
        this.mediaProgress = (ProgressBar) findViewById(R.id.mediacontroller_progress);
        if (this.mediaProgress instanceof SeekBar) {
            SeekBar seekBar = (SeekBar) this.mediaProgress;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        long newposition = (duration * progress) / 1000L;
//                        long newposition = 1000L * progress / duration;
                        LogUtil.i(TAG, "seekBar "+duration+"------"+newposition);
                        sendBroadcast(newposition);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            this.mediaProgress.setMax(1000);
        }

        this.play.setOnClickListener(this);
        this.last.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.player_pattern.setOnClickListener(this);
        this.panel_back.setOnClickListener(this);
        this.music_share.setOnClickListener(this);

        if (pattern == 10){
            circulation.setVisibility(View.VISIBLE);
            single.setVisibility(View.GONE);
            random.setVisibility(View.GONE);
        }else if (pattern==11){
            circulation.setVisibility(View.GONE);
            single.setVisibility(View.VISIBLE);
            random.setVisibility(View.GONE);
        }else if (pattern==12){
            circulation.setVisibility(View.GONE);
            single.setVisibility(View.GONE);
            random.setVisibility(View.VISIBLE);
        }

        music_name.setText(musicname);
        artist.setText(musicArtist);
        this.curMediaTime.setText(stringForTime(0));
        this.mediaTime.setText(stringForTime(0));

    }

    private void initView() {
        Bitmap bitmap = BitmapUtils.createAlbumArt(musicPath);
        this.blurview = (ImageView)findViewById(R.id.blur_view);

        this.civTest = (CircleImageView) findViewById(R.id.civ_test);
        this.civTest.setBorderColor(Color.GRAY);
        this.civTest.setBorderWidth(8);
        if (CommonUtil.isBlank(bitmap)){
            this.blurview.setImageResource(R.drawable.icon_album_dark);
            this.civTest.setImageResource(R.drawable.icon_album_default);
        }else {
//            this.blurview.setImageBitmap(bitmap);
            this.civTest.setImageBitmap(bitmap);
        }

    }

    private void updatePausePlay(boolean isPlaying) {
        if (isPlaying) {
            this.play.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            this.play.setImageResource(R.drawable.ic_play_white_36dp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != this.MReceiver) {
//            unregisterReceiver(this.MReceiver);
//        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void onClick(View v) {
        pattern = getPlayerPattern();
        switch (v.getId()) {
            case R.id.last:
                sendService(1);
                this.play.setImageResource(R.drawable.ic_play_white_36dp);
                break;
            case R.id.play:
                sendService(2);
                if (!CommonUtil.isBlank(musicPath)) {
                    if (isPlaying) {
                        isPlaying = false;
                    } else {
                        isPlaying = true;
                    }
                }
                updatePausePlay(isPlaying);
                break;
            case R.id.next:
                this.play.setImageResource(R.drawable.ic_play_white_36dp);
                sendService(3);
                break;
            case R.id.player_pattern:
                if (pattern == 10){
                    circulation.setVisibility(View.GONE);
                    single.setVisibility(View.VISIBLE);
                    setPlayerPattern(11);
                }else if (pattern==11){
                    single.setVisibility(View.GONE);
                    random.setVisibility(View.VISIBLE);
                    setPlayerPattern(12);
                }else if (pattern==12){
                    random.setVisibility(View.GONE);
                    circulation.setVisibility(View.VISIBLE);
                    setPlayerPattern(10);
                }
                break;
            case R.id.panel_back:
                onBackPressed();
                break;
            case R.id.music_share:
                showMorePanel();
                break;
        }
    }

    public static void setPlayerPattern(int pattern) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MusicService.UPDATE_MUSIC_PLAYER_PATTERN, Activity.MODE_PRIVATE);
        sp.edit().putInt("pattern", pattern).apply();
    }

    public static int getPlayerPattern() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MusicService.UPDATE_MUSIC_PLAYER_PATTERN, Activity.MODE_PRIVATE);
        return sp.getInt("pattern", 10);
    }

    private void sendBroadcast(long position) {
        Intent intent = new Intent();
        intent.setAction(MusicService.MUSIC_CURRENT_POSITION);
        Bundle bundle = new Bundle();
        bundle.putLong("newposition", position);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    public void sendService(int status) {
        if (!CommonUtil.isBlank(musicPath)) {
            Intent intentService = new Intent(MusicPlayerActivity.this, MusicService.class);
            Bundle bundle = new Bundle();
            bundle.putString("currentMusicPath", musicPath);
            bundle.putInt("status", status);
            bundle.putLong("songID", songID);
            intentService.putExtra("bundle", bundle);
            startService(intentService);
        } else {
            ToastUtils.showShort(getApplicationContext(), "请选你喜欢的歌曲播放");
        }
    }


    private void showMorePanel() {
        final PopupWindow popupWindow = new PopupWindow(this);
        View rootView = getLayoutInflater().inflate(R.layout.view_template_more_panel, null);
        final SeekBar light = (SeekBar) rootView.findViewById(R.id.progress_light);
        float lightProgress = getPreferences(MODE_PRIVATE).getFloat(SP_ARTICLE_BRIGHT_KEY, 0);
        if (lightProgress != 0) {
            light.setProgress((int) (lightProgress * 100));
        }
        light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float seekProgress = seekBar.getProgress();
                // 防止亮度为0导致一些机型屏幕完全变暗
                if (seekProgress == 0) {
                    seekProgress = 1.0f;
                }
                float bright = seekProgress / 100.0f;
                setScreenBrightness(bright);
                getPreferences(MODE_PRIVATE).edit().putFloat(SP_ARTICLE_BRIGHT_KEY, bright).commit();
            }
        });
        popupWindow.setContentView(rootView);
        popupWindow.setWidth(-1);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }
    private void setScreenBrightness(float bright) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = bright;
        getWindow().setAttributes(lp);
    }

}

