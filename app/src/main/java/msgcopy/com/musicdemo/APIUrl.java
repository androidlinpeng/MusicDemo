package msgcopy.com.musicdemo;

/**
 * Created by liang on 2017/4/13.
 */

public class APIUrl {

    public final static String URL_DOMAIN = "http://tingapi.ting.baidu.com/";
    public final static String path_song_list = "v1/restserver/ting?from=android&version=5.9.9.4&channel=xiaomi&operator=-1&method=baidu.ting.billboard.billCategory&format=json&kflag=2";
    public final static String path_new_song = "v1/restserver/ting?from=qianqian&version=5.9.9.&method=baidu.ting.billboard.billList&format=json&offset=0&size=10";
    public final static String path_song_url = "v1/restserver/ting?from=android&version=5.9.9.4&channel=xiaomi&operator=-1&method=baidu.ting.song.getInfos&format=json&songid=540175998&ts=1493091774293&e=hDK18Lg55jsbpgW8RY8PaAk1Dtt%2Bc%2FVO0VxH2UWWYNHgbo6Y1h3sXVgH%2B5ieEB8Z&nw=2&ucf=1&res=1&l2p=0&lpb=&usup=1&lebo=0";

}
