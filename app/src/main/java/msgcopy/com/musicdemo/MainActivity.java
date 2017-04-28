package msgcopy.com.musicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Timer;
import java.util.TimerTask;

import msgcopy.com.musicdemo.activity.BaseActivity;
import msgcopy.com.musicdemo.fragment.MainFragment;
import msgcopy.com.musicdemo.fragment.MusicHallFragment;
import msgcopy.com.musicdemo.fragment.SearchFragment;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.ViewUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static msgcopy.com.musicdemo.R.id.toolbar;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    private int currentTime;

    public final static int REQUEST_REG = 1;

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

        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mToolbar.setTitle(R.string.str_home);

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
        this.mediaProgress.setMax(1000);
        //清除默认的左右边距
        mediaProgress.setPadding(0, 0, 0, 0);
//        mediaProgress.setSecondaryProgress(mediaProgress.getMax());

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
                    sendService(2);
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
                    sendService(3);
                }
                break;
        }

    }

    private void initDefaultFragment() {
        mToolbar.setTitle(R.string.str_home);
        mFragmentManager = getSupportFragmentManager();
        mCurrentFragment = ViewUtils.createFragment(MainFragment.class, true);
        mFragmentManager.beginTransaction().add(R.id.frame_content, mCurrentFragment).commit();

        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        } else if (id == R.id.nav_search) {
            mToolbar.setTitle(R.string.str_search);
            backStackFragment(SearchFragment.class);
            mToolbar.setVisibility(View.GONE);
        } else if (id == R.id.nav_theme) {
            mToolbar.setTitle(R.string.str_theme);
        } else if (id == R.id.nav_music_hall) {
            mToolbar.setTitle(R.string.str_music_hall);
            switchFragment(MusicHallFragment.class);
        } else if (id == R.id.nav_collect) {
            mToolbar.setTitle(R.string.str_collect);
        } else if (id == R.id.nav_settings) {
            mToolbar.setTitle(R.string.str_settings);
        } else if (id == R.id.nav_drop_out) {
            mToolbar.setTitle(R.string.str_drop_out);
            stopService(new Intent(MainActivity.this, MusicService.class));
            finish();
        }
        item.setChecked(true);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                        Glide.with(getApplication()).load(ListenerUtil.getAlbumArtUri(song.albumId).toString())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.icon_album_default)
                                .centerCrop()
                                .into(imag_albumArt);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }

    private void sendService(int status) {
        Intent intentService = new Intent(MainActivity.this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", currentsong.path);
        bundle.putInt("status", status);
        bundle.putLong("songID", currentsong.id);
        intentService.putExtra("bundle", bundle);
        startService(intentService);
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
            switch (keyBackClickCount++) {
                case 0:
                    mToolbar.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().popBackStack();
//                    ToastUtils.showShort(this, R.string.str_press_again_to_exit);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 500);
                    break;
                case 1:
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (this.mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                this.mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                this.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
