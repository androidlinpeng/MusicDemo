package msgcopy.com.musicdemo.adapter;

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

import msgcopy.com.musicdemo.MusicPlayer;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.utils.FileUtils;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.LogUtil;


public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean withHeader;
    private float topPlayScore;
    private String action;

    public interface OnMoreClickListener {
        void onMoreClick(int position);
    }
    public OnMoreClickListener moreClickListener;

    public OnMoreClickListener setOnMoreClickListener(OnMoreClickListener Listener) {
        return moreClickListener = Listener;
    }

    public SongsListAdapter(AppCompatActivity context, List<Song> arraylist, String action, boolean withHeader) {
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
                View playShuffle = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_play_shuffle, viewGroup, false);
                viewHolder = new PlayShuffleViewHoler(playShuffle);
                break;
            case Type.TYPE_SONG:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_linear_layout_item, viewGroup, false);
                viewHolder = new ItemHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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

                if (ListenerUtil.getAlbumArtUri(localItem.albumId).toString()!=null) {
                    LogUtil.i("filePath1",localItem.artistName+"----"+ListenerUtil.getAlbumArtUri(localItem.albumId).toString());
                    Glide.with(holder.itemView.getContext()).load(ListenerUtil.getAlbumArtUri(localItem.albumId).toString())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .placeholder(R.drawable.icon_album_default)
                            .centerCrop()
                            .into(itemHolder.albumArt);
                }

                if (FileUtils.fileIsExistsAlbumPic(localItem.artistName, localItem.title)) {
                    String filePath = FileUtils.getAlbumDir() + FileUtils.getAlbumFileName(localItem.artistName, localItem.title);
                    LogUtil.i("filePath2",localItem.artistName+"----"+filePath);
                    Glide.with(holder.itemView.getContext()).load(filePath)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .placeholder(R.drawable.icon_album_default)
                            .centerCrop()
                            .into(itemHolder.albumArt);
                }

                if (topPlayScore != -1) {
                    itemHolder.playscore.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemHolder.playscore.getLayoutParams();
                }

                itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moreClickListener.onMoreClick(position-1);
                    }
                });

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
                    MyApplication.getInstance().getMusicService().updateMusicList(arraylist);
                    MusicPlayer.playAll(mContext,arraylist, getAdapterPosition() - 1);
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
