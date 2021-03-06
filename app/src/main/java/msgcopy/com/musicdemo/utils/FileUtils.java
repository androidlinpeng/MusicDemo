package msgcopy.com.musicdemo.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;

/**
 * Created by Administrator on 2017/4/29.
 */

public class FileUtils {

    private static final String MP3 = ".mp3";
    private static final String LRC = ".lrc";
    private static final String JPG = ".jpg";

    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + "/MusicDemo";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "/Music/";
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "/Lyric/";
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "MusicDemo/Music/";
        return mkdirs(dir);
    }

    public static String getAlbumDir() {
        String dir = getAppDir() + "/Album/";
        return mkdirs(dir);
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getAlbumFileName(String artist, String title) {
        return getFileName(artist, title)+JPG;
    }

    public static String getMp3FileName(String artist, String title) {
        return getFileName(artist, title) + MP3;
    }

    public static String getLrcFileName(String artist, String title) {
        return getFileName(artist, title) + LRC;
    }

    public static String getFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = MyApplication.getInstance().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = MyApplication.getInstance().getString(R.string.unknown);
        }
        return artist + "-" + title;
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static void saveLrcFile(String path, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmapFile(String fileName,Bitmap bitmap){
        try {
            File file = new File(fileName);
            File parentFlie = file.getParentFile();
            if (!parentFlie.exists()){
                parentFlie.mkdirs();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileIsExists(String artist, String title) {
        try {
            String filePath = getAppDir() + "/Lyric/" + getLrcFileName(artist, title);
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean fileIsExistsAlbumPic(String artist, String title) {
        try {
            String filePath = getAppDir() + "/Album/" + getAlbumFileName(artist, title);
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
