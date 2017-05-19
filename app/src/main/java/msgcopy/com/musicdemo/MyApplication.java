package msgcopy.com.musicdemo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import msgcopy.com.musicdemo.service.MusicService;

/**
 * Created by liang on 2017/4/12.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;

    private MusicService musicService;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Fresco.initialize(this);

    }
    public static MyApplication getInstance() {
        return myApplication;
    }

    public MusicService getMusicService(){
        return getInstance().musicService;
    }

    public MusicService setMusicService(MusicService musicService){
        return getInstance().musicService = musicService;
    }

}
