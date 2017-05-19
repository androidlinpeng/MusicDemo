package msgcopy.com.musicdemo.adapter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.utils.MsgCache;
import msgcopy.com.musicdemo.MusicPlayer;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.Songurl;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.LogUtil;
import rx.Subscriber;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class PlayerSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private float topPlayScore;

    private Subscriber<Songurl> subscriberGet;


    public PlayerSongListAdapter(AppCompatActivity context, List<Song> arraylist, String action, boolean withHeader) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;

        }
        this.mContext = context;
        this.songIDs = getSongIds();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_linear_layout_item, viewGroup, false);
        viewHolder = new ItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Song localItem;
        localItem = arraylist.get(position);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        itemHolder.album.setText(localItem.albumName);

        itemHolder.popupMenu.setVisibility(View.GONE);
        itemHolder.popupMenu.setBackgroundResource(R.drawable.ic_clear_white_36dp);
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    arraylist.remove(position);
                    if (arraylist.size()!= 0){
                        notifyItemRemoved(position);
                    }
                    MsgCache.get().put(Constants.MUSIC_LIST, arraylist);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        Glide.with(holder.itemView.getContext()).load(ListenerUtil.getAlbumArtUri(localItem.albumId).toString())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.icon_album_default)
                .centerCrop()
                .into(itemHolder.albumArt);

        if (topPlayScore != -1) {
            itemHolder.playscore.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemHolder.playscore.getLayoutParams();
        }


    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        int songNum = arraylist.size();
        long[] ret = new long[songNum];
        for (int i = 0; i < songNum; i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public void setSongList(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
        if (arraylist.size() != 0) {
            this.topPlayScore = arraylist.get(0).getPlayCountScore();
        }
        notifyDataSetChanged();
    }

    public long[] getSongListID() {
        return songIDs;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private TextView album;
        private ImageView albumArt;
        private ImageView popupMenu;
        private View playscore;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.text_item_title);
            this.artist = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.album = (TextView) view.findViewById(R.id.text_item_subtitle_2);
            this.albumArt = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.playscore = view.findViewById(R.id.playscore);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (arraylist.get(getAdapterPosition()).equals(Constants.LOCAL_MUSIC)) {
                        MyApplication.getInstance().getMusicService().updateMusicList(arraylist);
                        MusicPlayer.playAll(mContext, arraylist, getAdapterPosition());
                    }else {
                        MyApplication.getInstance().getMusicService().updateMusicList(arraylist);
                        getHttp(arraylist.get(getAdapterPosition()).id+"",getAdapterPosition());
                    }
                }
            }, 100);

        }
    }

    public void getHttp(final String songid, final int position) {
        LogUtil.i(TAG,"getHttp"+Long.parseLong(songid));
        //git请求
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
                MusicPlayer.PlaySong(mContext,songurl,position);
            }
        };
        new HttpUser().getSongPath(subscriberGet,songid);
    }

}
