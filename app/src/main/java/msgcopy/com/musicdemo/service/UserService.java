package msgcopy.com.musicdemo.service;

import msgcopy.com.musicdemo.modul.ArtistInfo;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liang on 2017/4/17.
 */

public interface UserService {
    String BASE_PARAMETERS_ARTIST = "?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";

    @GET(BASE_PARAMETERS_ARTIST)
    Observable<ArtistInfo> getArtistInfo(@Query("artist") String artist);
}











