package msgcopy.com.musicdemo.modul.online;

import java.io.Serializable;

/**
 * Created by liang on 2017/4/27.
 */

public class MusicHallTop implements Serializable{

    public String name;

    public int type;

    public MusicHallTop(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
