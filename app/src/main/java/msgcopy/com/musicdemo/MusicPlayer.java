package msgcopy.com.musicdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.service.MusicService;

import static msgcopy.com.musicdemo.Constants.MUSIC_LIST;

/**
 * Created by liang on 2017/4/26.
 */

public class MusicPlayer {

    public static void PlaySong(Context mContext, Songurl songurl){
        Intent intentService = new Intent(mContext, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", songurl.getBitrate().getFile_link());
        bundle.putString("songID", songurl.getSonginfo().getSong_id());
        bundle.putInt("status", 0);
        intentService.putExtra("bundle", bundle);
        mContext.startService(intentService);

        Song song = new Song(Constants.ONLINE_MUSIC,Long.parseLong(songurl.getSonginfo().getSong_id()), -1, -1, songurl.getSonginfo().getTitle(), songurl.getSonginfo().getAuthor(), songurl.getSonginfo().getAlbum_title(), -1, -1,songurl.getBitrate().getFile_link());
        MsgCache.get().put(Constants.MUSIC_INFO,song);

    }

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
        MsgCache.get().put(MUSIC_LIST, arraylist);
    }

    public static void onLinePlayAll(Context mContext, Songurl songurl,List<NewSong.SongListBean> arraylist){
        Intent intentService = new Intent(mContext, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", songurl.getBitrate().getFile_link());
        bundle.putString("songID", songurl.getSonginfo().getSong_id());
        bundle.putInt("status", 0);
        intentService.putExtra("bundle", bundle);
        mContext.startService(intentService);

        Song song = new Song(Constants.ONLINE_MUSIC,Long.parseLong(songurl.getSonginfo().getSong_id()), -1, -1, songurl.getSonginfo().getTitle(), songurl.getSonginfo().getAuthor(), songurl.getSonginfo().getAlbum_title(), -1, -1,songurl.getBitrate().getFile_link());

        MsgCache.get().put(Constants.MUSIC_INFO,song);

        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < arraylist.size(); i++) {
            Song song1 = new Song(Long.parseLong(arraylist.get(i).getSong_id()), -1, -1, arraylist.get(i).getTitle(), arraylist.get(i).getAuthor(), arraylist.get(i).getAlbum_title(), -1, -1);
            songs.add(song1);
        }
        MsgCache.get().put(Constants.MUSIC_LIST, songs);

//        MsgCache.get().put(Constants.ONLINE_MUSIC_INFO, songurl);
//        MsgCache.get().put(Constants.ONLINE_MUSIC_PLAYER_LIST, arraylist);
    }

    public static void onLinePlay(Context mContext, Songurl songurl){
        Intent intentService = new Intent(mContext, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentMusicPath", songurl.getBitrate().getFile_link());
        bundle.putString("songID", songurl.getSonginfo().getSong_id());
        bundle.putInt("status", 0);
        intentService.putExtra("bundle", bundle);
        mContext.startService(intentService);

        Song song = new Song(Constants.ONLINE_MUSIC,Long.parseLong(songurl.getSonginfo().getSong_id()), -1, -1, songurl.getSonginfo().getTitle(), songurl.getSonginfo().getAuthor(), songurl.getSonginfo().getAlbum_title(), -1, -1,songurl.getBitrate().getFile_link());
        MsgCache.get().put(Constants.MUSIC_INFO,song);
    }

    public static Songurl getOnLineMusicInfo() {
        return (Songurl) MsgCache.get().getAsObject(Constants.ONLINE_MUSIC_INFO);
    }

    public static List<NewSong.SongListBean> getOnLineMusicList() {
        return (List<NewSong.SongListBean>) MsgCache.get().getAsObject(Constants.ONLINE_MUSIC_PLAYER_LIST);
    }

    public static void setOnlineStatus(boolean status){
        SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(Constants.ONLINE_STATUS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.ONLINE_STATUS,status);
        editor.commit();
    }
    public static boolean getOnlineStatus(){
        SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(Constants.ONLINE_STATUS,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.ONLINE_STATUS,false);
    }

    public static Song getMusicInfo() {
        return (Song) MsgCache.get().getAsObject(Constants.MUSIC_INFO);
    }

    public static List<Song> getMusicList() {
        return (List<Song>) MsgCache.get().getAsObject(Constants.MUSIC_LIST);
    }

    public static void setPlayerPattern(int pattern) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MusicService.UPDATE_MUSIC_PLAYER_PATTERN, Activity.MODE_PRIVATE);
        sp.edit().putInt("pattern", pattern).apply();
    }

    public static int getPlayerPattern() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MusicService.UPDATE_MUSIC_PLAYER_PATTERN, Activity.MODE_PRIVATE);
        return sp.getInt("pattern", 10);
    }

}
