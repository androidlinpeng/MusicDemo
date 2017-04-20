package msgcopy.com.musicdemo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.List;

import msgcopy.com.musicdemo.LogUtil;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.MusicPlayerActivity;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.dataloader.SongLoader;
import msgcopy.com.musicdemo.fragment.SongsFragment;
import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

                        PlayState playState = new PlayState(currentMusicPath,currentTime,mediaTime,isPlaying);
                        RxBus.getInstance().post(playState);

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
        pattern = MusicPlayerActivity.getPlayerPattern();
        mediaPlayer = new MediaPlayer();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_CURRENT_POSITION);
        registerReceiver(this.myReceiver, filter);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pattern = MusicPlayerActivity.getPlayerPattern();
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
        pattern = MusicPlayerActivity.getPlayerPattern();
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
    }


    /**
     * 播放音乐
     */
    private void play(String path) {
        try {
            LogUtil.i("musicplayID",""+songID+currentMusicPath);
            SongLoader.getSongForID(MyApplication.getInstance(),songID)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Song>() {
                        @Override
                        public void call(Song song) {
                            LogUtil.i("musicplayID","songID:"+song.title+"----------"+currentMusicPath);
                            RxBus.getInstance().post(song);
                        }
                    });
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
                                break;
                            } else {
                                currentMusicPath = mlist.get(size - 1).path;
                                infoEntity = mlist.get(size - 1);
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
                                break;
                            } else {
                                currentMusicPath = mlist.get(0).path;
                                infoEntity = mlist.get(0);
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
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();//停止音频的播放
                }
                mediaPlayer.release();//释放资源
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
                    LogUtil.i(TAG, "currentposition" + position);
                }
            }
        }
    }
}
