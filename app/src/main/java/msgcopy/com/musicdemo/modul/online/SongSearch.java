package msgcopy.com.musicdemo.modul.online;

import java.util.List;

/**
 * Created by Administrator on 2017/4/29.
 */

public class SongSearch {


    /**
     * song : [{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"860","songname":"你是谁","songid":"580862","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"孟庭苇","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"29058dcfe095848ce41L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"260","songname":"你是谁","songid":"277358691","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"刘美麟","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"93081088286309582feceaL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"30","songname":"你是谁","songid":"274961225","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"龙飞凤","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"110810639349095812f8caL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"10","songname":"你是谁的谁","songid":"87874717","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"洪立","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"400753cdc9d09571f1221L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"0","songname":"你是谁","songid":"275502318","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"Choeyuna","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"6908106bd4ee095819ca29L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"0","songname":"你是谁呀 (Feat. Samuel Seo)","songid":"275376756","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"issac Squab","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"39081069ea74095818b2dcL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"0","songname":"你是谁","songid":"275529400","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"儿童合唱团","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"2508106c3eb809581a2aefL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"0","songname":"你是谁（feat。狒狒）（BOA（宝儿））（MR）","songid":"275432896","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"singerTech","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"4608106ac5c00958194cd7L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"0","songname":"你是谁爱谁谁","songid":"267033812","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"孙艺程","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"2307fea9cd40957a06f7fL"},{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"0","songname":"你是谁","songid":"285294","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"黄湘怡","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"750545a6e095848d5f5L"}]
     * order : song,album
     * error_code : 22000
     * album : [{"albumname":"你是谁","weight":"30","artistname":"龙飞凤","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/d3d513cd63ca7965520116efd53ff795/274961227/274961227.jpg@s_0,w_40","albumid":"274961224"},{"albumname":"你是谁我是谁","weight":"0","artistname":"刘潭","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/105668754/105668754.jpg@s_0,w_40","albumid":"105673504"},{"albumname":"你是谁","weight":"0","artistname":"The Who","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/lrc/30fd606901437b4eb251040895feeb0c/184273/184273.jpg@s_0,w_40","albumid":"184273"}]
     */

    private String order;
    private int error_code;
    private List<SongBean> song;
    private List<AlbumBean> album;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<SongBean> getSong() {
        return song;
    }

    public void setSong(List<SongBean> song) {
        this.song = song;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public static class SongBean {
        /**
         * bitrate_fee : {"0":"129|-1","1":"-1|-1"}
         * weight : 860
         * songname : 你是谁
         * songid : 580862
         * has_mv : 0
         * yyr_artist : 0
         * resource_type_ext : 0
         * artistname : 孟庭苇
         * info :
         * resource_provider : 1
         * control : 0000000000
         * encrypted_songid : 29058dcfe095848ce41L
         */

        private String bitrate_fee;
        private String weight;
        private String songname;
        private String songid;
        private String has_mv;
        private String yyr_artist;
        private String resource_type_ext;
        private String artistname;
        private String info;
        private String resource_provider;
        private String control;
        private String encrypted_songid;

        public String getBitrate_fee() {
            return bitrate_fee;
        }

        public void setBitrate_fee(String bitrate_fee) {
            this.bitrate_fee = bitrate_fee;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public String getSongid() {
            return songid;
        }

        public void setSongid(String songid) {
            this.songid = songid;
        }

        public String getHas_mv() {
            return has_mv;
        }

        public void setHas_mv(String has_mv) {
            this.has_mv = has_mv;
        }

        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getResource_provider() {
            return resource_provider;
        }

        public void setResource_provider(String resource_provider) {
            this.resource_provider = resource_provider;
        }

        public String getControl() {
            return control;
        }

        public void setControl(String control) {
            this.control = control;
        }

        public String getEncrypted_songid() {
            return encrypted_songid;
        }

        public void setEncrypted_songid(String encrypted_songid) {
            this.encrypted_songid = encrypted_songid;
        }
    }

    public static class AlbumBean {
        /**
         * albumname : 你是谁
         * weight : 30
         * artistname : 龙飞凤
         * resource_type_ext : 0
         * artistpic : http://qukufile2.qianqian.com/data2/pic/d3d513cd63ca7965520116efd53ff795/274961227/274961227.jpg@s_0,w_40
         * albumid : 274961224
         */

        private String albumname;
        private String weight;
        private String artistname;
        private String resource_type_ext;
        private String artistpic;
        private String albumid;

        public String getAlbumname() {
            return albumname;
        }

        public void setAlbumname(String albumname) {
            this.albumname = albumname;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getAlbumid() {
            return albumid;
        }

        public void setAlbumid(String albumid) {
            this.albumid = albumid;
        }
    }
}
