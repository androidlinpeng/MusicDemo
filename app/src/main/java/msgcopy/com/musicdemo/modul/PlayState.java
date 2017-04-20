package msgcopy.com.musicdemo.modul;

/**
 * Created by liang on 2017/4/20.
 */

public class PlayState {

    public String currentPath;
    public int currentTime;
    public int mediaTime;
    public boolean isPlaying;

    public PlayState(String currentPath, int currentTime, int mediaTime, boolean isPlaying) {
        this.currentPath = currentPath;
        this.currentTime = currentTime;
        this.mediaTime = mediaTime;
        this.isPlaying = isPlaying;
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
}
