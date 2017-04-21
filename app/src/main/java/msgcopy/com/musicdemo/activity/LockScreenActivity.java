package msgcopy.com.musicdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.LogUtil;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.CommonUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LockScreenActivity extends AppCompatActivity {

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

    private Song currentsong;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            currentsong = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);

        if (!CommonUtil.isBlank(currentsong)) {
            lockscreentitle.setText(currentsong.title);
            lockscreensubtitle.setText(currentsong.artistName);
            lockscreensubtitle_2.setText(currentsong.albumName);
        }
        subscribeChangedSong();
        subscribePlayState();

    }

    @OnClick({R.id.lock_screen_play,R.id.lock_screen_last,R.id.lock_screen_next})
    public void LockScreen(ImageView imag){
        switch (imag.getId()){
            case R.id.lock_screen_last:
                if (!CommonUtil.isBlank(currentsong)) {
                    this.lockscreenplay.setImageResource(R.drawable.ic_play_white_36dp);
                    sendService(1);
                }
                break;
            case R.id.lock_screen_play:
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
            case R.id.lock_screen_next:
                if (!CommonUtil.isBlank(currentsong)) {
                    this.lockscreenplay.setImageResource(R.drawable.ic_play_white_36dp);
                    sendService(3);
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
                        lockscreentitle.setText(song.title);
                        lockscreensubtitle.setText(song.artistName);
                        lockscreensubtitle_2.setText(song.albumName);
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
//                .distinctUntilChanged()
                .subscribe(new Action1<PlayState>() {
                    @Override
                    public void call(PlayState playState) {
                        isPlaying = playState.isPlaying();
                        updatePausePlay(isPlaying);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

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
        super.onBackPressed();
    }
}
