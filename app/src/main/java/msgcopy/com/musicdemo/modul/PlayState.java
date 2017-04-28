package msgcopy.com.musicdemo.modul;

/**
 * Created by liang on 2017/4/20.
 */

public class PlayState {

    public String currentPath;
    public int currentTime;
    public int mediaTime;
    public boolean isPlaying;
    public boolean onLine;

    public PlayState(String currentPath, int currentTime, int mediaTime, boolean isPlaying, boolean onLine) {
        this.currentPath = currentPath;
        this.currentTime = currentTime;
        this.mediaTime = mediaTime;
        this.isPlaying = isPlaying;
        this.onLine = onLine;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public int getMediaTime() {
        return mediaTime;
    }

    public void setMediaTime(int mediaTime) {
        this.mediaTime = mediaTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
