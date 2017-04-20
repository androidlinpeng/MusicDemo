package msgcopy.com.musicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import msgcopy.com.musicdemo.fragment.MainFragment;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.ViewUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;

    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private ActionBarDrawerToggle mDrawerToggle;  //菜单开关
    private Toolbar mToolbar;
    private NavigationView mNavigationView;//侧边菜单项
    private RelativeLayout playerbottom;
    private ImageView imgalbumArt;
    private ImageView imag_albumArt;
    private TextView songtitle;
    private TextView text_song_title;
    private TextView text_song_artist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setTitle(R.string.str_home);

        playerbottom = (RelativeLayout)findViewById(R.id.player_bottom);
        imag_albumArt = (ImageView) findViewById(R.id.imag_albumArt);
        text_song_title = (TextView) findViewById(R.id.text_song_title);
        text_song_artist = (TextView)findViewById(R.id.text_song_artist);

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

    }

    public void onClick(View view){
        startActivity(new Intent(this,MusicPlayerActivity.class));
    }


    private void initDefaultFragment() {
        mToolbar.setTitle(R.string.str_home);
        mFragmentManager = getSupportFragmentManager();
        mCurrentFragment = ViewUtils.createFragment(MainFragment.class);
        mFragmentManager.beginTransaction().add(R.id.frame_content,mCurrentFragment).commit();

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
        if (id == R.id.action_settings) {
            return true;
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
        } else if (id == R.id.nav_theme) {
//            startActivity(new Intent(this, ChangeThemeSkinActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        item.setChecked(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void switchFragment(Class<?> mclass){
        Fragment fragment = ViewUtils.createFragment(mclass);
        if (fragment.isAdded()){
            mFragmentManager.beginTransaction().hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
        }else {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.frame_content,fragment).commitAllowingStateLoss();
        }
        mCurrentFragment = fragment;
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
                        text_song_title.setText(""+song.title);
                        text_song_artist.setText(""+song.artistName);
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
        stopService(new Intent(this,MusicService.class));
    }
}
