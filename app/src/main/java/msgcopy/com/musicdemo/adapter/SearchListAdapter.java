package msgcopy.com.musicdemo.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.service.MusicService;
import msgcopy.com.musicdemo.utils.ListenerUtil;

/**
 * Created by liang on 2017/4/21.
 */

public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean withHeader;
    private float topPlayScore;
    private String action;

    public SearchListAdapter(AppCompatActivity context, List<Song> arraylist, String action, boolean withHeader) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;

        }
        this.mContext = context;
        this.songIDs = getSongIds();
        this.withHeader = withHeader;
        this.action = action;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && withHeader) {
            return Type.TYPE_PLAY_SHUFFLE;
        } else {
            return Type.TYPE_SONG;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case Type.TYPE_PLAY_SHUFFLE:
                View playShuffle = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_clauses, viewGroup, false);
                viewHolder = new PlayShuffleViewHoler(playShuffle);
                break;
            case Type.TYPE_SONG:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_search_layout_item, viewGroup, false);
                viewHolder = new ItemHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case Type.TYPE_PLAY_SHUFFLE:

                break;
            case Type.TYPE_SONG:
                ItemHolder itemHolder = (ItemHolder) holder;
                Song localItem;
                if (withHeader) {
                    localItem = arraylist.get(position - 1);
                } else {
                    localItem = arraylist.get(position);
                }

                itemHolder.title.setText(localItem.title);
                itemHolder.artist.setText(localItem.artistName);
                itemHolder.album.setText(localItem.albumName);

                Glide.with(holder.itemView.getContext()).load(ListenerUtil.getAlbumArtUri(localItem.albumId).toString())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.icon_album_default)
                        .centerCrop()
                        .into(itemHolder.albumArt);

                if (topPlayScore != -1) {
                    itemHolder.playscore.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemHolder.playscore.getLayoutParams();
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        if (withHeader && arraylist.size() != 0) {
            return (null != arraylist ? arraylist.size() + 1 : 0);
        } else {
            return (null != arraylist ? arraylist.size() : 0);
        }
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


    public static class Type {
        public static final int TYPE_PLAY_SHUFFLE = 0;
        public static final int TYPE_SONG = 1;
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
                    Song song = arraylist.get(getAdapterPosition() - 1);
                    Intent intentService = new Intent(mContext, MusicService.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("currentMusicPath", song.path);
                    bundle.putLong("songID", song.id);
                    bundle.putInt("status", 0);
                    intentService.putExtra("bundle", bundle);
                    mContext.startService(intentService);
                    MsgCache.get().put(Constants.MUSIC_INFO, song);
                }
            }, 100);

        }
    }

    public class PlayShuffleViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener {
        public PlayShuffleViewHoler(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 100);
        }
    }
}
