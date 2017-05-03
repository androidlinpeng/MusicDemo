package msgcopy.com.musicdemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.MusicPlayer;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.activity.LockScreenActivity;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.utils.BitmapUtils;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.LogUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;
import msgcopy.com.musicdemo.utils.ToastUtils;
import rx.Subscriber;

import static msgcopy.com.musicdemo.Constants.NOTIC_CANCEL;

/**
 * Created by liang on 2017/2/13.
 */
public class MusicService extends Service {

    private static final String TAG = "MusicService";

    public static final String UPDATE_MUSIC_PLAYER_PATTERN = "update_music_player_pattern";

    public static final String MUSIC_CURRENT_POSITION = "music_current_position";  //当前音乐播放时间更新动作

    public static final String MUSIC_INFO = "current_music_info";

    private MyReceiver myReceiver;  //自定义广播接收器

    private MediaPlayer mediaPlayer;

    private String currentMusicPath = "";

    private int currentTime;        //当前播放进度

    private int mediaTime;        //播放时间

    private boolean isPlaying = false;

    private boolean isPreparing = false;

    private int status;

    private int pattern;

    private long songID;

    private List<Song> mlist = null;

    private int mPlayPosition;

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind:");
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void last() {
        LogUtil.i(TAG, "last:");
        if (mlist.isEmpty()) {
            return;
        }
        Song song = null;
        switch (MusicPlayer.getPlayerPattern()) {
            case Constants.PLAYTER_PATTERN_RANDOM://随机
                mPlayPosition = new Random().nextInt(mlist.size());
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
            case Constants.PLAYTER_PATTERN_SINGLE://单曲
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
            case Constants.PLAYTER_PATTERN_CIRCULATION://循环
                mPlayPosition = mPlayPosition - 1 >= 0 ? mPlayPosition - 1 : mlist.size() - 1;
                LogUtil.i(TAG, "last:" + mPlayPosition);
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
        }

    }

    public void pause(Intent intent) {
        LogUtil.i(TAG, "pause:");

    }

    public void play(Song song, int position) {
        try {
            LogUtil.i(TAG, "play:");
            mPlayPosition = position;
            if (song.type.equals(Constants.LOCAL_MUSIC)) {
                RxBus.getInstance().post(song);
                isPreparing =true;
                PreferencesUtility.getInstance(MyApplication.getInstance()).setPlaySongID(song.id);
                currentMusicPath = song.path;
                mediaPlayer.reset();// 把各项参数恢复到初始状态
                mediaPlayer.setDataSource(song.path);
                mediaPlayer.prepare(); // 进行缓冲
                mediaPlayer.setOnPreparedListener(new PreparedListener());// 注册一个监听器
                mediaTime = mediaPlayer.getDuration();
                handler.sendEmptyMessage(1);
                MsgCache.get().put(MUSIC_INFO, song);
                initNotificationBar();
            } else {
                getHttp(song.id, song);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (mediaPlayer.isPlaying() && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        } else if (isPreparing && mediaPlayer != null){
            mediaPlayer.start();
            isPlaying = true;
        }else {
            mPlayPosition = updatePlayingPosition();
            play(mlist.get(mPlayPosition),mPlayPosition);
        }
        handler.sendEmptyMessage(1);
        initNotificationBar();
    }

    public void next() {
        LogUtil.i(TAG, "next:");
        if (mlist.isEmpty()) {
            return;
        }
        Song song = null;
        switch (MusicPlayer.getPlayerPattern()) {
            case Constants.PLAYTER_PATTERN_RANDOM://随机
                mPlayPosition = new Random().nextInt(mlist.size());
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
            case Constants.PLAYTER_PATTERN_SINGLE://单曲
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
            case Constants.PLAYTER_PATTERN_CIRCULATION://循环
                mPlayPosition = (mPlayPosition + 1) > mlist.size() - 1 ? 0 : (mPlayPosition + 1);
                song = mlist.get(mPlayPosition);
                play(song, mPlayPosition);
                break;
        }

    }

    public void updateMusicList(List<Song> list) {
        LogUtil.i(TAG, "updateMusicList:");
        mlist = list;

    }

    public int updatePlayingPosition() {
        int position = 0;
        long id = PreferencesUtility.getInstance(MyApplication.getInstance()).getPlaySongID();
        for (int i = 0; i < mlist.size(); i++) {
            if (mlist.get(i).id == id) {
                position = i;
                break;
            }
        }
        mPlayPosition = position;
        PreferencesUtility.getInstance(MyApplication.getInstance()).setPlaySongID(mlist.get(mPlayPosition).id);
        return mPlayPosition;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mediaPlayer != null) {
                        try {
                            currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                            if (currentTime>=mediaTime){
                                next();
                            }
                            handler.sendEmptyMessageDelayed(1, 500);
                            PlayState playState = new PlayState(currentMusicPath, currentTime, mediaTime, isPlaying, false);
                            RxBus.getInstance().post(playState);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate ");
        pattern = MusicPlayer.getPlayerPattern();
        mediaPlayer = new MediaPlayer();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_CURRENT_POSITION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(this.myReceiver, filter);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.i(TAG, "onStart ");
    }


    public String getHttp(final long songid, final Song song) {
        //git请求
        Subscriber<Songurl> subscriberGet = new Subscriber<Songurl>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted:");
            }

            @Override
            public void onError(Throwable onError) {
                Log.i(TAG, "onError:" + onError.getMessage());
            }

            @Override
            public void onNext(Songurl songurl) {
                Log.i(TAG, "onNext:");
                try {
                    currentMusicPath = songurl.getBitrate().getFile_link();
                    mediaPlayer.reset();// 把各项参数恢复到初始状态
                    mediaPlayer.setDataSource(currentMusicPath);
                    mediaPlayer.prepare(); // 进行缓冲
                    mediaPlayer.setOnPreparedListener(new PreparedListener());// 注册一个监听器
                    mediaTime = mediaPlayer.getDuration();
                    handler.sendEmptyMessage(1);
                    RxBus.getInstance().post(song);
                    isPreparing =true;
                    PreferencesUtility.getInstance(MyApplication.getInstance()).setPlaySongID(song.id);
                    MsgCache.get().put(MUSIC_INFO, song);
                    initNotificationBar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpUser().getSongPath(subscriberGet, songid + "");

        return currentMusicPath;
    }


    @Override
    public boolean stopService(Intent name) {
        LogUtil.i(TAG, "stopService:");
        return super.stopService(name);

    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy:");
        super.onDestroy();
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();//停止音频的播放
                }
                mediaPlayer.release();//释放资源
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        unregisterReceiver(this.myReceiver);
        MyApplication.getInstance().setMusicService(null);
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start(); // 开始播放
            if (mediaPlayer.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
            initNotificationBar();
        }
    }

    public void initNotificationBar() {
        Song song = null;
        try {
            song = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!CommonUtil.isBlank(song)) {
            Notification notification = new Notification();
            //初始化通知
            notification.icon = R.drawable.icon_album_default;
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_control);
            notification.contentView = contentView;

            Bitmap bitmap = BitmapUtils.createAlbumArt(song.path);
            if (bitmap != null) {
                contentView.setImageViewBitmap(R.id.imag_albumArt, bitmap);
            } else {
                contentView.setImageViewResource(R.id.imag_albumArt, R.drawable.icon_album_default);
            }
            if (!CommonUtil.isBlank(song)) {
                contentView.setTextViewText(R.id.notic_song_title, "" + song.title);
                contentView.setTextViewText(R.id.notic_song_artist, "" + song.artistName);
            }

            Intent intentPlay = new Intent(Constants.NOTIC_PLAY);//新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
            PendingIntent pIntentPlay = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
            contentView.setImageViewResource(R.id.img_notic_play, getPlayIconRes(isPlaying));
            contentView.setOnClickPendingIntent(R.id.img_notic_play, pIntentPlay);//为play控件注册事件
            Intent intentPause = new Intent(Constants.NOTIC_PAUSE);
//          PendingIntent pIntentPause = PendingIntent.getBroadcast(this, 0, intentPause, 0);
//          contentView.setOnClickPendingIntent(R.id.bt_notic_pause, pIntentPause);
            Intent intentNext = new Intent(Constants.NOTIC_NEXT);
            PendingIntent pIntentNext = PendingIntent.getBroadcast(this, 0, intentNext, 0);
            contentView.setOnClickPendingIntent(R.id.img_notic_next, pIntentNext);
            Intent intentLast = new Intent(Constants.NOTIC_LAST);
            PendingIntent pIntentLast = PendingIntent.getBroadcast(this, 0, intentLast, 0);
            contentView.setOnClickPendingIntent(R.id.img_notic_last, pIntentLast);
            Intent intentCancel = new Intent(NOTIC_CANCEL);
            PendingIntent pIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, 0);
            contentView.setOnClickPendingIntent(R.id.img_notic_cancel, pIntentCancel);
            notification.flags = notification.FLAG_NO_CLEAR;//设置通知点击或滑动时不被清除
            NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.MESSAGE_CENTER_NOTIFY_ID, notification);//开启通知

        }
    }

    private static int getPlayIconRes(boolean isPlaying) {
        if (isPlaying) {
            return R.drawable.ic_pause_white_36dp;
        } else {
            return R.drawable.ic_play_white_36dp;
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (action.equals(MUSIC_CURRENT_POSITION)) {
                    Bundle bundle = intent.getExtras();
                    long position = bundle.getLong("newposition");
                    mediaPlayer.seekTo((int) position);
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    Intent lockscreen = new Intent(MusicService.this, LockScreenActivity.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockscreen);
                } else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    if (intent.hasExtra("state")) {
                        int state = intent.getIntExtra("state", 0);
                        if (state == 1) {//插入耳机

                        } else if (state == 0) {//拔出耳机
                            sendService(2);
                        }
                    }
                }
            }
        }

    }

    private void sendService(int status) {
        if (!CommonUtil.isBlank(currentMusicPath)) {
            Intent intentService = new Intent(MusicService.this, MusicService.class);
            Bundle bundle = new Bundle();
            bundle.putString("currentMusicPath", currentMusicPath);
            bundle.putInt("status", status);
            bundle.putLong("songID", songID);
            intentService.putExtra("bundle", bundle);
            startService(intentService);
        } else {
            ToastUtils.showShort(getApplicationContext(), "请选你喜欢的歌曲播放");
        }
    }
}
