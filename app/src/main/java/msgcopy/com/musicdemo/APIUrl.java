package msgcopy.com.musicdemo;

/**
 * Created by liang on 2017/4/13.
 */

public class APIUrl {

    public final static String URL_DOMAIN = "http://tingapi.ting.baidu.com/";
    public final static String path_song_list = "v1/restserver/ting?from=android&version=5.9.9.4&channel=xiaomi&operator=-1&method=baidu.ting.billboard.billCategory&format=json&kflag=2";
    public final static String path_new_song = "v1/restserver/ting?from=qianqian&version=5.9.9.&method=baidu.ting.billboard.billList&format=json&offset=0";
    public final static String path_song_url = "v1/restserver/ting?from=qianqian&version=5.9.9.&method=baidu.ting.song.play";
    public final static String path_song_lry = "v1/restserver/ting?from=qianqian&version=5.9.9.&method=baidu.ting.song.lry";
    public final static String path_song_search = "v1/restserver/ting?from=qianqian&version=5.9.9.&method=baidu.ting.search.catalogSug";

}
