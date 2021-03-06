package msgcopy.com.musicdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.modul.online.SongLry;
import msgcopy.com.musicdemo.utils.FileUtils;
import rx.Subscriber;

/**
 * Created by liang on 2017/5/3.
 */

public abstract class DownloadOnlineMusic extends DownloadMusic {

    private static final String TAG = "DownloadOnlineMusic";

    private NewSong.SongListBean songListBean;

    private Subscriber<Songurl> subscriberGet;
    private Subscriber<SongLry> subscriberSongLry;
    private Subscriber<Bitmap> subscriberPic;

    public DownloadOnlineMusic(Activity activity, NewSong.SongListBean songListBean) {
        super(activity);
        this.songListBean = songListBean;
    }

    @Override
    protected void download() {

        final String title = songListBean.getTitle();
        final String artist = songListBean.getArtist_name();
        subscriberGet = new Subscriber<Songurl>() {
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
                downloadMusic(songurl.getBitrate().getFile_link(), artist, title);
            }
        };
        new HttpUser().getSongPath(subscriberGet, songListBean.getSong_id());

        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(songListBean.getLrclink()) && !lrcFile.exists()) {
            subscriberSongLry = new Subscriber<SongLry>() {
                @Override
                public void onCompleted() {
                    Log.i(TAG, "onCompleted:");
                }

                @Override
                public void onError(Throwable onError) {
                    Log.i(TAG, "onError:" + onError.getMessage());
                }

                @Override
                public void onNext(SongLry songLry) {
                    Log.i(TAG, "onNext:");
                    String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(artist, title);
                    FileUtils.saveLrcFile(filePath, songLry.getLrcContent());
                }
            };
            new HttpUser().getSongLry(subscriberSongLry, songListBean.getSong_id());
        }

        // 下载封面
        String albumFileName = FileUtils.getAlbumFileName(artist, title);
        File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        String picUrl = songListBean.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            picUrl = songListBean.getPic_small();
        }
        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
            downloadAlbum(picUrl, albumFile);
        }

    }

    private void downloadAlbum(String picUrl, final File albumFile) {
        subscriberPic = new Subscriber<Bitmap>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: download");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError:download" + e.getMessage());
            }

            @Override
            public void onNext(Bitmap bitmap) {
                Log.i(TAG, "onNext:download" + bitmap);
            }
        };
        new HttpUser().getDownloadPicFromNet(subscriberPic, picUrl, albumFile);
    }
}








