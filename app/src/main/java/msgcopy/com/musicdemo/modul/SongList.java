package msgcopy.com.musicdemo.modul;

import java.util.List;

/**
 * Created by liang on 2017/4/25.
 */

public class SongList {

    private int error_code;
    private List<ContentBeanX> content;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<ContentBeanX> getContent() {
        return content;
    }

    public void setContent(List<ContentBeanX> content) {
        this.content = content;
    }

    public static class ContentBeanX {
        /**
         * name : 新歌榜
         * type : 1
         * count : 4
         * comment : 该榜单是根据百度音乐平台歌曲每日播放量自动生成的数据榜单，统计范围为近期发行的歌曲，每日更新一次
         * web_url :
         * pic_s192 : http://b.hiphotos.baidu.com/ting/pic/item/9922720e0cf3d7caf39ebc10f11fbe096b63a968.jpg
         * pic_s444 : http://d.hiphotos.baidu.com/ting/pic/item/78310a55b319ebc4845c84eb8026cffc1e17169f.jpg
         * pic_s260 : http://b.hiphotos.baidu.com/ting/pic/item/e850352ac65c1038cb0f3cb0b0119313b07e894b.jpg
         * pic_s210 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_c49310115801d43d42a98fdc357f6057.jpg
         * content : [{"title":"暧昧","author":"薛之谦","song_id":"540175998","album_id":"540175996","album_title":"暧昧","rank_change":"0","all_rate":"64,128,256,320,flac"},{"title":"动物世界","author":"薛之谦","song_id":"539843843","album_id":"539843841","album_title":"动物世界","rank_change":"0","all_rate":"64,128,256,320,flac"},{"title":"慢慢习惯","author":"刘德华","song_id":"540130926","album_id":"540130924","album_title":"慢慢习惯","rank_change":"0","all_rate":"64,128,256,320,flac"},{"title":"碰碰碰","author":"章小军","song_id":"540362160","album_id":"540362158","album_title":"碰碰碰","rank_change":"2","all_rate":"64,128,256,320,flac"}]
         * pic_s328 : http://business.cdn.qianqian.com/qianqian/file/574d65433ab76_334.JPG
         * pic_s640 : http://y.baidu.com/cms/app/640-640.jpg
         */

        private String name;
        private int type;
        private int count;
        private String comment;
        private String web_url;
        private String pic_s192;
        private String pic_s444;
        private String pic_s260;
        private String pic_s210;
        private String pic_s328;
        private String pic_s640;
        private List<ContentBean> content;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getWeb_url() {
            return web_url;
        }

        public void setWeb_url(String web_url) {
            this.web_url = web_url;
        }

        public String getPic_s192() {
            return pic_s192;
        }

        public void setPic_s192(String pic_s192) {
            this.pic_s192 = pic_s192;
        }

        public String getPic_s444() {
            return pic_s444;
        }

        public void setPic_s444(String pic_s444) {
            this.pic_s444 = pic_s444;
        }

        public String getPic_s260() {
            return pic_s260;
        }

        public void setPic_s260(String pic_s260) {
            this.pic_s260 = pic_s260;
        }

        public String getPic_s210() {
            return pic_s210;
        }

        public void setPic_s210(String pic_s210) {
            this.pic_s210 = pic_s210;
        }

        public String getPic_s328() {
            return pic_s328;
        }

        public void setPic_s328(String pic_s328) {
            this.pic_s328 = pic_s328;
        }

        public String getPic_s640() {
            return pic_s640;
        }

        public void setPic_s640(String pic_s640) {
            this.pic_s640 = pic_s640;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            /**
             * title : 暧昧
             * author : 薛之谦
             * song_id : 540175998
             * album_id : 540175996
             * album_title : 暧昧
             * rank_change : 0
             * all_rate : 64,128,256,320,flac
             */

            private String title;
            private String author;
            private String song_id;
            private String album_id;
            private String album_title;
            private String rank_change;
            private String all_rate;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getSong_id() {
                return song_id;
            }

            public void setSong_id(String song_id) {
                this.song_id = song_id;
            }

            public String getAlbum_id() {
                return album_id;
            }

            public void setAlbum_id(String album_id) {
                this.album_id = album_id;
            }

            public String getAlbum_title() {
                return album_title;
            }

            public void setAlbum_title(String album_title) {
                this.album_title = album_title;
            }

            public String getRank_change() {
                return rank_change;
            }

            public void setRank_change(String rank_change) {
                this.rank_change = rank_change;
            }

            public String getAll_rate() {
                return all_rate;
            }

            public void setAll_rate(String all_rate) {
                this.all_rate = all_rate;
            }
        }
    }
}
