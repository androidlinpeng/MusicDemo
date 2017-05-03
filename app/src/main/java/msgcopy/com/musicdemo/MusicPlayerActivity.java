package msgcopy.com.musicdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import me.wcy.lrcview.LrcView;
import msgcopy.com.musicdemo.activity.BaseActivity;
import msgcopy.com.musicdemo.adapter.MyViewPagerAdapter;
import msgcopy.com.musicdemo.adapter.PlayerSongListAdapter;
import msgcopy.com.musicdemo.fragment.SongsFragment;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.online.SongLry;
import msgcopy.com.musicdemo.modul.online.SongSearch;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.BitmapUtils;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.FileUtils;
import msgcopy.com.musicdemo.utils.LogUtil;
import msgcopy.com.musicdemo.view.BlurringView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.R.attr.path;
import static msgcopy.com.musicdemo.Constants.PLAYTER_PATTERN_CIRCULATION;
import static msgcopy.com.musicdemo.Constants.PLAYTER_PATTERN_RANDOM;
import static msgcopy.com.musicdemo.Constants.PLAYTER_PATTERN_SINGLE;
import static msgcopy.com.musicdemo.MusicPlayer.getPlayerPattern;

public class MusicPlayerActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    private static final String TAG = "MusicPlayerActivity";

    private final static String SP_ARTICLE_BRIGHT_KEY = "article_bright_display";

    private BlurringView blurringView;

    private ImageView player_list;

    private ImageView bluredView = null;

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

    private ProgressBar mediaProgress = null;

    private StringBuilder mFormatBuilder;

    private Formatter mFormatter;

    private boolean isPlaying = false;

    private String musicname = null;

    private String musicArtist = null;

    private String musicPath = null;

    private int position;

    private int duration;

    private int pattern;

    private long songID;

    private LrcView mLrcView;

    private LrcView mLrcViewFull;

    private CirclePageIndicator circlePageIndicator=null;

    private Subscriber<SongSearch> subscriberSearch;

    private Subscriber<SongLry> subscriberSongLry;

    private ViewPager viewPager;

    private Song songPlay;

    private List<View> VPlist;

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
            songPlay = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
            this.musicname = songPlay.title;
            this.musicArtist = songPlay.artistName;
            this.musicPath = songPlay.path;
            this.songID = songPlay.id;

        } catch (Exception e) {
            e.printStackTrace();
        }
        pattern = getPlayerPattern();

        setContentView(R.layout.activity_music_player);

        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        initViewPager();
        initMediaController();
        initView();

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicService.MUSIC_PLAYER_STATE);
//        this.registerReceiver(this.MReceiver, intentFilter);

        subscribeChangedSong();
        subscribePlayState();

        getSongSearch();
    }

    public void getSongSearch() {
        loadLrc("");
        setLrcLabel("搜索歌词中...");
        if (FileUtils.fileIsExists(musicArtist, musicname)) {
            String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(musicArtist, musicname);
            loadLrc(filePath);
        } else {
            String query = musicArtist + "" + musicname;
            subscriberSearch = new Subscriber<SongSearch>() {
                @Override
                public void onCompleted() {
                    Log.i(TAG, "onCompleted:");
                }

                @Override
                public void onError(Throwable onError) {
                    Log.i(TAG, "onError:" + onError.getMessage());
                    loadLrc("");
                    setLrcLabel("暂无歌词");
                }

                @Override
                public void onNext(SongSearch songSearch) {
                    Log.i(TAG, "onNext:");
                    getSongLry(songSearch.getSong().get(0).getSongid());

                }
            };
            new HttpUser().getSongSearch(subscriberSearch, query);
        }
    }

    public void getSongLry(String songid) {

        subscriberSongLry = new Subscriber<SongLry>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted:");
            }

            @Override
            public void onError(Throwable onError) {
                Log.i(TAG, "onError:" + onError.getMessage());
                loadLrc("");
                setLrcLabel("暂无歌词");
            }

            @Override
            public void onNext(SongLry songLry) {
                Log.i(TAG, "onNext:");
                String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(musicArtist, musicname);
                FileUtils.saveLrcFile(filePath, songLry.getLrcContent());
                loadLrc(filePath);
            }
        };
        new HttpUser().getSongLry(subscriberSongLry, songid);
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcView.loadLrc(file);
        mLrcViewFull.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        mLrcView.setLabel(label);
        mLrcViewFull.setLabel(label);
    }

    public void playerSongList() {
        final PopupWindow popupWindow = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.view_player_song_list, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        ImageView playdelete = (ImageView) view.findViewById(R.id.play_list_delete);
        final PlayerSongListAdapter mAdapter = new PlayerSongListAdapter(this, null, "", true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setSongList(SongsFragment.getMusicList());
        playdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MsgCache.get().remove(Constants.MUSIC_LIST);
                popupWindow.dismiss();
                MsgCache.get().clear();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("Result", true);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                stopService(new Intent(MusicPlayerActivity.this, MusicService.class));
            }
        });

        popupWindow.setContentView(view);
        popupWindow.setWidth(-1);
        popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels * 2 / 3);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
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
                        musicname = song.title;
                        musicArtist = song.artistName;
                        musicPath = song.path;
                        songID = song.id;
                        music_name.setText(musicname);
                        artist.setText(musicArtist);
                        initView();
                        getSongSearch();
                        LogUtil.i(TAG,"musicPath"+musicPath+"-----------------"+path);
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
//                .distinctUntilChanged()
                .subscribe(new Action1<PlayState>() {
                    @Override
                    public void call(PlayState playState) {
                        if (mediaProgress != null) {
                            String path = playState.getCurrentPath();
                            position = playState.getCurrentTime();
                            duration = playState.getMediaTime();
                            isPlaying = playState.isPlaying();
                            if (mediaProgress != null) {
                                if (duration > 0) {
                                    long pos = 1000L * position / duration;
                                    //显示播放进度
                                    mediaProgress.setProgress((int) pos);
                                    mLrcView.updateTime(position);
                                    mLrcViewFull.updateTime(position);
                                }
                            }
                            if (curMediaTime != null) {
                                curMediaTime.setText(stringForTime(position));
                            }
                            if (mediaTime != null) {
                                mediaTime.setText(stringForTime(duration));
                            }
                            updatePausePlay(isPlaying);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        View coverView = LayoutInflater.from(getAppContext()).inflate(R.layout.fragment_play_centre_pager_cover,null);
        View lrcView = LayoutInflater.from(getAppContext()).inflate(R.layout.fragment_play_centre_pager_lrc,null);
        mLrcView = (LrcView) coverView.findViewById(R.id.lrc_view);
        civTest = (CircleImageView) coverView.findViewById(R.id.civ_test);
        civTest.setBorderColor(R.color.transparent);

        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
        civTest.setBorderWidth(8);
        VPlist = new ArrayList<View>();
        VPlist.add(coverView);
        VPlist.add(lrcView);
        viewPager.setAdapter(new MyViewPagerAdapter(VPlist));

        circlePageIndicator = (CirclePageIndicator)findViewById(R.id.circle_vpi);
        circlePageIndicator.setOnPageChangeListener(this);
        circlePageIndicator.setStrokeWidth(0);
        circlePageIndicator.setFillColor(getResources().getColor(R.color.white));
        circlePageIndicator.setPageColor(getResources().getColor(R.color.indicator_color));
        circlePageIndicator.setViewPager(viewPager);

    }

    private void initMediaController() {
        this.player_list = (ImageView) findViewById(R.id.imag_player_list);
        this.music_share = (ImageView) findViewById(R.id.music_share);
        this.panel_back = (ImageView) findViewById(R.id.panel_back);
        this.player_pattern = (RelativeLayout) findViewById(R.id.player_pattern);
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
                        sendBroadcast(newposition);
                        mLrcView.updateTime(progress);
                        mLrcViewFull.updateTime(progress);
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

        this.player_list.setOnClickListener(this);
        this.play.setOnClickListener(this);
        this.last.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.player_pattern.setOnClickListener(this);
        this.panel_back.setOnClickListener(this);
        this.music_share.setOnClickListener(this);

        if (pattern == 10) {
            circulation.setVisibility(View.VISIBLE);
            single.setVisibility(View.GONE);
            random.setVisibility(View.GONE);
        } else if (pattern == 11) {
            circulation.setVisibility(View.GONE);
            single.setVisibility(View.VISIBLE);
            random.setVisibility(View.GONE);
        } else if (pattern == 12) {
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
        this.bluredView = (ImageView) findViewById(R.id.blur_view);
        this.blurringView = (BlurringView) findViewById(R.id.blurring_view);
        this.blurringView.setBlurRadius(8);
        this.blurringView.setDownsampleFactor(8);

        if (CommonUtil.isBlank(bitmap)) {
            this.bluredView.setImageResource(R.drawable.icon_album_dark);
            civTest.setImageResource(R.drawable.icon_album_default);
        } else {
            this.bluredView.setImageBitmap(bitmap);
            civTest.setImageBitmap(bitmap);
        }
        bluredView.setBackgroundResource(R.color.transparent);
        blurringView.setBlurredView(bluredView);
        blurringView.invalidate();

    }

    private void updatePausePlay(boolean isPlaying) {
        if (isPlaying) {
            this.play.setImageResource(R.drawable.play_btn_pause_selector);
        } else {
            this.play.setImageResource(R.drawable.play_btn_play_pause_selector);
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
        pattern = MusicPlayer.getPlayerPattern();
        switch (v.getId()) {
            case R.id.last:
                this.play.setImageResource(R.drawable.ic_play_white_36dp);
                MyApplication.getInstance().getMusicService().last();
                break;
            case R.id.play:
                MyApplication.getInstance().getMusicService().play();
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
                MyApplication.getInstance().getMusicService().next();
                break;
            case R.id.player_pattern:
                if (pattern == PLAYTER_PATTERN_CIRCULATION) {
                    circulation.setVisibility(View.GONE);
                    single.setVisibility(View.VISIBLE);
                    MusicPlayer.setPlayerPattern(PLAYTER_PATTERN_SINGLE);
                } else if (pattern == PLAYTER_PATTERN_SINGLE) {
                    single.setVisibility(View.GONE);
                    random.setVisibility(View.VISIBLE);
                    MusicPlayer.setPlayerPattern(PLAYTER_PATTERN_RANDOM);
                } else if (pattern == PLAYTER_PATTERN_RANDOM) {
                    random.setVisibility(View.GONE);
                    circulation.setVisibility(View.VISIBLE);
                    MusicPlayer.setPlayerPattern(PLAYTER_PATTERN_CIRCULATION);
                }
                break;
            case R.id.panel_back:
                onBackPressed();
                break;
            case R.id.music_share:
                showMorePanel();
                break;
            case R.id.imag_player_list:
                playerSongList();
                break;
        }
    }

    private void sendBroadcast(long position) {
        Intent intent = new Intent();
        intent.setAction(MusicService.MUSIC_CURRENT_POSITION);
        Bundle bundle = new Bundle();
        bundle.putLong("newposition", position);
        intent.putExtras(bundle);
        sendBroadcast(intent);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class ItemListDivider extends RecyclerView.ItemDecoration {

        private Drawable drawable = null;

        public ItemListDivider(Context cxt) {
            this.drawable = cxt.getResources().getDrawable(R.drawable.divider_article_list);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + drawable.getIntrinsicHeight();

                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }
    }

}

