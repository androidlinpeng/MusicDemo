package msgcopy.com.musicdemo.service;

import msgcopy.com.musicdemo.APIUrl;
import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.modul.SongList;
import msgcopy.com.musicdemo.modul.Songurl;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liang on 2017/4/17.
 */

public interface UserService {

    @GET(APIUrl.path_song_url)
    Observable<Songurl> getSongPath();

    @GET(APIUrl.path_song_list)
    Observable<SongList> getSongListData();

    @GET(APIUrl.path_new_song)
    Observable<NewSong> getGetData(@Query("type") String type);
}











