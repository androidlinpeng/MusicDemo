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

import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.MusicPlayer;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.activity.LockScreenActivity;
import msgcopy.com.musicdemo.fragment.SongsFragment;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.utils.BitmapUtils;
import msgcopy.com.musicdemo.utils.CommonUtil;
import msgcopy.com.musicdemo.utils.LogUtil;
import msgcopy.com.musicdemo.utils.ToastUtils;
import rx.Subscriber;

import static msgcopy.com.musicdemo.Constants.NOTIC_CANCEL;

/**
 * Created by liang on 2017/2/13.
 */
public class MusicService extends Service {

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作

    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作

    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作

    private static final String TAG = "MusicService";

    public static final String UPDATE_MUSIC_PLAYER_PATTERN = "update_music_player_pattern";

    public static final String MUSIC_CURRENT_POSITION = "music_current_position";  //当前音乐播放时间更新动作

    public static final String MUSIC_SCREEN_OFF = "music_screen_off";

    public static final String MUSIC_PLAYER_STATE = "music_player_state";

    public static final String MUSIC_INFO = "current_music_info";


    private MyBinder mBinder = new MyBinder();

    private MyReceiver myReceiver;  //自定义广播接收器

    private MediaPlayer mediaPlayer;

    private String currentMusicPath = "";

    private int currentTime;        //当前播放进度

    private int mediaTime;        //播放时间

    private boolean isPlaying = false;

    private int status;

    private int pattern;

    private long songID;

    private List<Song> mlist = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mediaPlayer != null) {
                        try {
                            currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
//                        Intent intent = new Intent();
//                        intent.setAction(MUSIC_PLAYER_STATE);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("currentMusicPath", currentMusicPath);
//                        bundle.putInt("currentTime", currentTime);
//                        bundle.putInt("mediaTime", mediaTime);
//                        bundle.putBoolean("isPlaying", isPlaying);
//                        intent.putExtras(bundle);
//                        sendBroadcast(intent);
                            handler.sendEmptyMessageDelayed(1, 100);
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
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

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
                pattern = MusicPlayer.getPlayerPattern();
                if (pattern == 10) {
                    sendPlayerStatus(3, pattern);
                } else if (pattern == 11) {
                    mediaPlayer.start();
                } else if (pattern == 12) {
                    sendPlayerStatus(12, 12);
                }
            }
        });

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.i(TAG, "onStart ");
        Bundle bundle = intent.getBundleExtra("bundle");
        if (null != bundle) {
            this.currentMusicPath = bundle.getString("currentMusicPath");
            this.status = bundle.getInt("status");
            this.songID = bundle.getLong("songID");
        }
        pattern = MusicPlayer.getPlayerPattern();
        if (this.status == 0) {
            play(currentMusicPath);
        } else if (this.status == 1) {
            sendPlayerStatus(1, pattern);
        } else if (this.status == 2) {
            if (currentTime <= 0) {
                play(currentMusicPath);
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    mediaPlayer.start();
                    isPlaying = true;
                }
            }
        } else if (this.status == 3) {
            sendPlayerStatus(3, pattern);
        }
        initNotificationBar();
    }

    public String getHttp(final String songid) {
        LogUtil.i(TAG, "getHttp" + Long.parseLong(songid));
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
                try {
                    currentMusicPath = songurl.getBitrate().getFile_link();
                    mediaPlayer.reset();// 把各项参数恢复到初始状态
                    mediaPlayer.setDataSource(currentMusicPath);
                    mediaPlayer.prepare(); // 进行缓冲
                    mediaPlayer.setOnPreparedListener(new PreparedListener());// 注册一个监听器
                    mediaTime = mediaPlayer.getDuration();
                    handler.sendEmptyMessage(1);
                    RxBus.getInstance().post((Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpUser().getSongPath(subscriberGet, songid);

        return currentMusicPath;
    }


    /**
     * 播放音乐
     */
    private void play(String path) {
        try {

            Song song = (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
            if (song.type.equals(Constants.LOCAL_MUSIC)){
                RxBus.getInstance().post(song);
                mediaPlayer.reset();// 把各项参数恢复到初始状态
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare(); // 进行缓冲
                mediaPlayer.setOnPreparedListener(new PreparedListener());// 注册一个监听器
                mediaTime = mediaPlayer.getDuration();
                handler.sendEmptyMessage(1);
            }else {
                getHttp(song.id+"");
            }
//            SongLoader.getSongForID(MyApplication.getInstance(), songID)
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Action1<Song>() {
//                        @Override
//                        public void call(Song song) {
//                            RxBus.getInstance().post(song);
//                        }
//                    });
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare(); // 进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener());// 注册一个监听器
            mediaTime = mediaPlayer.getDuration();
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机数
     */
    private int getRandomIndex(int end) {
        int current = (int) (Math.random() * end);
        return current;
    }

    private void sendPlayerStatus(int status, int pattern) {
        mlist = SongsFragment.getMusicList();
        Song infoEntity = null;
        if (pattern == 10 || pattern == 11) {
            if (mlist.size() > 0) {
                int size = mlist.size();
                if (status == 1) {
                    for (int i = 0; i < size; i++) {
                        if (currentMusicPath.equals(mlist.get(i).path)) {
                            if (i - 1 >= 0) {
                                currentMusicPath = mlist.get(i - 1).path;
                                infoEntity = mlist.get(i - 1);
                                songID = mlist.get(i - 1).id;
                                break;
                            } else {
                                currentMusicPath = mlist.get(size - 1).path;
                                infoEntity = mlist.get(size - 1);
                                songID = mlist.get(size - 1).id;
                                break;
                            }
                        }
                    }
                } else if (status == 3) {
                    for (int i = 0; i < size; i++) {
                        if (currentMusicPath.equals(mlist.get(i).path)) {
                            if (i + 1 < size) {
                                currentMusicPath = mlist.get(i + 1).path;
                                infoEntity = mlist.get(i + 1);
                                songID = mlist.get(i + 1).id;
                                break;
                            } else {
                                currentMusicPath = mlist.get(0).path;
                                infoEntity = mlist.get(0);
                                songID = mlist.get(0).id;
                                break;
                            }
                        }
                    }
                }
            }
        } else if (pattern == 12) {
            int current = getRandomIndex(mlist.size());
            currentMusicPath = mlist.get(current).path;
            infoEntity = mlist.get(current);
        }
        MsgCache.get().put(MUSIC_INFO, infoEntity);
        play(currentMusicPath);

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

    private class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
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
