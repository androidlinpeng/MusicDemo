package msgcopy.com.musicdemo;

import msgcopy.com.musicdemo.modul.PlayState;
import msgcopy.com.musicdemo.modul.Song;

/**
 * Created by liang on 2017/5/12.
 */

public interface OnPlayerListener {

    void OnChangedSong(Song song);

    void onChengedProgress(PlayState playState);

}
