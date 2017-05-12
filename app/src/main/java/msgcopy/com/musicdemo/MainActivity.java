package msgcopy.com.musicdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.wcy.lrcview.LrcView;
import msgcopy.com.musicdemo.activity.BaseActivity;
import msgcopy.com.musicdemo.fragment.MainFragment;
import msgcopy.com.musicdemo.fragment.MusicHallFragment;
import msgcopy.com.musicdemo.fragment.SearchFragment;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.FileUtils;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.LogUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;
import msgcopy.com.musicdemo.utils.ToastUtils;
import msgcopy.com.musicdemo.utils.ViewUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static msgcopy.com.musicdemo.R.id.toolbar;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private FragmentManager mFragmentManager;
    public static Fragment mCurrentFragment;

    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private ActionBarDrawerToggle mDrawerToggle;  //菜单开关
    public static Toolbar mToolbar;
    private AppBarLayout appBar;
    private NavigationView mNavigationView;//侧边菜单项
    private RelativeLayout playerbottom;
    private ImageView imgalbumArt;
    private ImageView imag_albumArt;
    private ImageView imag_player_bottom;
    private ImageView imag_player_next;
    private TextView songtitle;
    private TextView text_song_title;
    private TextView text_song_artist;

    private SeekBar mediaProgress = null;

    private Song currentsong;

    private boolean isPlaying = false;

    public final static int REQUEST_REG = 1;

    private MainBroadcastReceiver mainReceiver;

    private ServiceConnection myServiceConnection;

    private PreferencesUtility mPreferences;

    private LrcView lrcView;
    private WindowManager windowM;
    private WindowManager.LayoutParams layoutParams;

    private class MainBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (null != action) {
                    if (action.equals(Constants.NOTIC_PLAY)) {
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
                    } else if (action.equals(Constants.NOTIC_NEXT)) {
                        if (!CommonUtil.isBlank(currentsong)) {
                            imag_player_bottom.setImageResource(R.drawable.ic_play_white_36dp);
                            MyApplication.getInstance().getMusicService().next();
                        }
                    } else if (action.equals(Constants.NOTIC_LAST)) {
                        if (!CommonUtil.isBlank(currentsong)) {
                            imag_player_bottom.setImageResource(R.drawable.ic_play_white_36dp);
                            MyApplication.getInstance().getMusicService().last();
                        }
                    } else if (action.equals(Constants.NOTIC_CANCEL)) {

                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //软键盘弹出时底部布局上移问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try {
            currentsong = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        mPreferences = PreferencesUtility.getInstance(this);

        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

//        mToolbar.setTitle(R.string.str_home);

        initView();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        imgalbumArt = (ImageView) headerView.findViewById(R.id.imgalbumArt);
        songtitle = (TextView) headerView.findViewById(R.id.songtitle);

        initDefaultFragment();
        subscribeChangedSong();
        subscribePlayState();

        mainReceiver = new MainBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NOTIC_PLAY);
        filter.addAction(Constants.NOTIC_PAUSE);
        filter.addAction(Constants.NOTIC_NEXT);
        filter.addAction(Constants.NOTIC_LAST);
        filter.addAction(Constants.NOTIC_CANCEL);
        registerReceiver(mainReceiver, filter);

        binPlayService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (windowM!=null) {
            windowM.removeView(lrcView);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //getRawX/Y 是获取相对于Device的坐标位置 注意区别getX/Y[相对于View]
                layoutParams.x = (int) event.getRawX();
                layoutParams.y = (int) event.getRawY();
                //更新"桌面歌词"的位置
                windowM.updateViewLayout(lrcView, layoutParams);
                //下面的removeView 可以去掉"桌面歌词"
                //wm.removeView(myView);
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.x = (int) event.getRawX();
                layoutParams.y = (int) event.getRawY();
                windowM.updateViewLayout(lrcView, layoutParams);
                break;
        }
        return false;
    }

    private void desktopShowView() {
        windowM = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = 1200;
        layoutParams.height = 200;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        lrcView = new LrcView(this);
        lrcView.setMinimumHeight(100);
        windowM.addView(lrcView, layoutParams);
        lrcView.setOnClickListener(this);

        if (!CommonUtil.isBlank(currentsong)) {
            loadLrc("");
//            setLrcLabel("搜索歌词中...");
            if (FileUtils.fileIsExists(currentsong.artistName, currentsong.title)) {
                String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(currentsong.artistName, currentsong.title);
                LogUtil.i("filePath",""+filePath);
                loadLrc(filePath);
            }
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        lrcView.loadLrc(file);
    }
    private void setLrcLabel(String label) {
        lrcView.setLabel(label);
    }

    private void binPlayService() {
        if (MyApplication.getInstance().getMusicService() == null) {
            Intent intent = new Intent();
            intent.setClass(this, MusicService.class);
            myServiceConnection = new PlayServiceConnection();
            bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private class PlayServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService musicService = ((MusicService.MyBinder) iBinder).getService();
            MyApplication.getInstance().setMusicService(musicService);
            try {
                List<Song> mlist = (List<Song>) MsgCache.get().getAsObject(Constants.MUSIC_LIST);
                musicService.updateMusicList(mlist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private final static int MESSAGE_CENTER_NOTIFY_ID = 2;

    private static int getPlayIconRes(boolean isPlaying) {
        if (isPlaying) {
            return R.drawable.ic_pause_white_36dp;
        } else {
            return R.drawable.ic_play_white_36dp;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_REG:
                    if (data.getExtras().getBoolean("Result")) {
                        currentsong = null;
                        initView();
                    }
                    break;
            }
        }
    }

    private void initView() {
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        playerbottom = (RelativeLayout) findViewById(R.id.player_bottom);
        imag_albumArt = (ImageView) findViewById(R.id.imag_albumArt);
        imag_player_bottom = (ImageView) findViewById(R.id.imag_player_bottom);
        text_song_title = (TextView) findViewById(R.id.text_song_title);
        text_song_artist = (TextView) findViewById(R.id.text_song_artist);
        this.mediaProgress = (SeekBar) findViewById(R.id.seek_song_touch);
        this.mediaProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    lrcView.updateTime(progress);
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
        //清除默认的左右边距
        this.mediaProgress.setPadding(0, 0, 0, 0);
        // mediaProgress.setSecondaryProgress(mediaProgress.getMax());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!CommonUtil.isBlank(currentsong)) {
                    Glide.with(getApplication()).load(ListenerUtil.getAlbumArtUri(currentsong.albumId).toString())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .placeholder(R.drawable.icon_album_default)
                            .centerCrop()
                            .into(imag_albumArt);
                    text_song_title.setText("" + currentsong.title);
                    text_song_artist.setText("" + currentsong.artistName);
                }
            }
        }, 1000);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_bottom:
                if (!CommonUtil.isBlank(currentsong)) {
                    startActivityForResult(new Intent(this, MusicPlayerActivity.class), REQUEST_REG);
                    overridePendingTransition(R.anim.leftin, R.anim.leftout);
                }
                break;
            case R.id.imag_player_bottom:
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
            case R.id.imag_player_next:
                if (!CommonUtil.isBlank(currentsong)) {
                    this.imag_player_bottom.setImageResource(R.drawable.ic_play_white_36dp);
                    MyApplication.getInstance().getMusicService().next();
                }
                break;
        }

    }

    private void initDefaultFragment() {
        mFragmentManager = getSupportFragmentManager();
        if (mPreferences.getStartMusicMode().equals(Constants.LOCAL_MUSIC)) {
            mToolbar.setTitle(R.string.str_home);
            mCurrentFragment = ViewUtils.createFragment(MainFragment.class, true);
            mNavigationView.getMenu().getItem(0).setChecked(true);
        } else {
            mToolbar.setTitle(R.string.str_music_hall);
            mCurrentFragment = ViewUtils.createFragment(MusicHallFragment.class, true);
            mNavigationView.getMenu().getItem(1).setChecked(true);
        }
        mFragmentManager.beginTransaction().add(R.id.frame_content, mCurrentFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:

                break;
            case R.id.action_balancer:
                MediaPlayer mediaPlayer = MyApplication.getInstance().getMusicService().mediaPlayer;
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.getAudioSessionId());
                this.startActivityForResult(effects, 666);
                break;
            case R.id.action_search:
                mToolbar.setTitle(R.string.str_search);
                backStackFragment(SearchFragment.class);
                mToolbar.setVisibility(View.GONE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            mToolbar.setTitle(R.string.str_home);
            switchFragment(MainFragment.class);
            mPreferences.setStartMusicMode(Constants.LOCAL_MUSIC);
        } else if (id == R.id.nav_search) {
            mToolbar.setTitle(R.string.str_search);
            backStackFragment(SearchFragment.class);
            mToolbar.setVisibility(View.GONE);
        } else if (id == R.id.nav_theme) {
            mToolbar.setTitle(R.string.str_theme);
        } else if (id == R.id.nav_music_hall) {
            mToolbar.setTitle(R.string.str_music_hall);
            switchFragment(MusicHallFragment.class);
            mPreferences.setStartMusicMode(Constants.ONLINE_MUSIC);
        } else if (id == R.id.nav_collect) {
            mToolbar.setTitle(R.string.str_collect);
        } else if (id == R.id.nav_settings) {
            mToolbar.setTitle(R.string.str_settings);
        } else if (id == R.id.nav_drop_out) {
            mToolbar.setTitle(R.string.str_drop_out);
            finishMain();
        }
        item.setChecked(true);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backStackFragment(Class<?> mclass) {
        Fragment fragment = ViewUtils.createFragment(mclass, false);
        mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.frame_content, fragment).addToBackStack(null).commit();
        mCurrentFragment = fragment;
    }

    public Fragment switchFragment(Class<?> mclass) {
        Fragment fragment = ViewUtils.createFragment(mclass, true);
        if (fragment.isAdded()) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
        } else {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.frame_content, fragment).commitAllowingStateLoss();
        }
        mCurrentFragment = fragment;
        return mCurrentFragment;
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
                        if (song.type.equals(Constants.LOCAL_MUSIC)) {
                            Glide.with(getApplication()).load(ListenerUtil.getAlbumArtUri(song.albumId).toString())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.icon_album_default)
                                    .centerCrop()
                                    .into(imag_albumArt);
                        } else {
                            Glide.with(getApplication()).load(ListenerUtil.getAlbumArtUri(song.albumId).toString())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.icon_album_default)
                                    .centerCrop()
                                    .into(imag_albumArt);
                        }
                        text_song_title.setText("" + song.title);
                        text_song_artist.setText("" + song.artistName);
                        currentsong = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void subscribePlayState() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PlayState.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PlayState>() {
                    @Override
                    public void call(PlayState playState) {
                        if (mediaProgress != null) {
                            int position = playState.getCurrentTime();
                            int duration = playState.getMediaTime();
                            if (duration > 0) {
                                long pos = 1000L * position / duration;
                                //显示播放进度
                                mediaProgress.setProgress((int) pos);
                                isPlaying = playState.isPlaying();
                                updatePausePlay(isPlaying);
                                lrcView.updateTime(position);
                                LogUtil.i("PlayState","------------------");
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

    private void finishMain() {
        RxBus.getInstance().unSubscribe(this);
        if (myServiceConnection != null) {
            unbindService(myServiceConnection);
        }
        if (mainReceiver != null) {
            unregisterReceiver(mainReceiver);
        }
        finish();
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updatePausePlay(boolean isPlaying) {
        if (isPlaying) {
            this.imag_player_bottom.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            this.imag_player_bottom.setImageResource(R.drawable.ic_play_white_36dp);
        }
    }

    private int keyBackClickCount;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            switch (keyBackClickCount++) {
                case 0:
                    ToastUtils.showLong(this, R.string.str_press_again_to_exit);
                    mToolbar.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().popBackStack();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 1000);
                    break;
                case 1:
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    desktopShowView();
                    return true;
                default:
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
