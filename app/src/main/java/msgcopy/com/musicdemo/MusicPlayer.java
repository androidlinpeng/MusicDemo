package msgcopy.com.musicdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;

/**
 * Created by liang on 2017/4/26.
 */

public class MusicPlayer {

    public static void playAll(Context context, List<Song> arraylist, int getAdapterPosition){
        Song song = arraylist.get(getAdapterPosition);
        Intent intentService = new Intent(context, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", song.path);
        bundle.putLong("songID", song.id);
        bundle.putInt("status", 0);
        intentService.putExtra("bundle", bundle);
        context.startService(intentService);
        MsgCache.get().put(Constants.MUSIC_INFO, song);
        MsgCache.get().put(Constants.MUSIC_LIST, arraylist);
    }

}
