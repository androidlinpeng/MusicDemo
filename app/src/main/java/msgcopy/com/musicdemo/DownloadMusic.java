package msgcopy.com.musicdemo;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import msgcopy.com.musicdemo.utils.FileUtils;

/**
 * Created by liang on 2017/5/3.
 */

public abstract class DownloadMusic implements IExecutor<Void>{

    private Activity activity;

    public DownloadMusic(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        downloadWrapper();
    }

    private void downloadWrapper() {
        onPrepare();
        download();
    }

    protected abstract void download();

    protected static void downloadMusic(String url,String artist,String song){
        String fileName = FileUtils.getMp3FileName(artist,song);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(FileUtils.getFileName(artist,song));
        request.setDescription("正在下载...");
        request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), fileName);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// 不允许漫游
        DownloadManager downloadManager = (DownloadManager) MyApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);
//        AppCache.getDownloadList().put(id, song);
    }
}













