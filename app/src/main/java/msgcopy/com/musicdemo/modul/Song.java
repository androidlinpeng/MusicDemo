package msgcopy.com.musicdemo.modul;

import java.io.Serializable;

/**
 * Created by hefuyi on 2016/11/3.
 */

public class Song implements Serializable{

    public String type = "local_music";
    public final long albumId;
    public final String albumName;
    public final long artistId;
    public final String artistName;
    public final int duration;
    public final long id;
    public final String title;
    public final int trackNumber;
    public float playCountScore;
    public String path;
    public String picsmall;

    public Song() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.trackNumber = -1;
        this.path = "";
    }

    public Song(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.path = "";
    }

    public Song(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber, String _path) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.path = _path;
    }

    public Song(String type, long id, long albumId, long artistId, String title, String artistName, String albumName, int duration, int trackNumber, String path) {
        this.type = type;
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.path = path;
    }
    public Song(String type, long id, long albumId, long artistId, String title, String artistName, String albumName, int duration, int trackNumber, String path,String picsmall) {
        this.type = type;
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.path = path;
        this.picsmall = picsmall;
    }

    public Song(String type, long id, long albumId, long artistId, String title, String artistName, String albumName, int duration, int trackNumber) {
        this.type = type;
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.path = "";
    }

    public void setPlayCountScore(float playCountScore) {
        this.playCountScore = playCountScore;
    }

    public float getPlayCountScore() {
        return playCountScore;
    }

}
