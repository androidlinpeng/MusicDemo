package msgcopy.com.musicdemo.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import msgcopy.com.musicdemo.MusicPlayer;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.utils.ListenerUtil;

/**
 * Created by hefuyi on 2016/12/3.
 */

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    private long albumID;
    private long[] songIDs;

    public AlbumSongsAdapter(Activity context, long albumID) {
        this.mContext = context;
        this.albumID = albumID;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_linear_layout_item, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumSongsAdapter.ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        itemHolder.album.setText(localItem.albumName);

        Glide.with(mContext)
                .load(ListenerUtil.getAlbumArtUri(localItem.albumId).toString())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.icon_album_default)
                .centerCrop()
                .into(itemHolder.albumArt);

//        setOnPopupMenuListener(itemHolder, i);
    }



    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public void setSongList(List<Song> songList) {
        arraylist = songList;
        songIDs = getSongIds();
        notifyDataSetChanged();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private TextView album;
        private ImageView albumArt;
        private ImageView popupMenu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.text_item_title);
            this.artist = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.album = (TextView) view.findViewById(R.id.text_item_subtitle_2);
            this.albumArt = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    MusicPlayer.playAll(mContext,arraylist, getAdapterPosition());

//                    MsgCache.get().put(Constants.MUSIC_LIST, arraylist);
//                    Song song = arraylist.get(getAdapterPosition());
//                    Intent intentService = new Intent(mContext, MusicService.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("currentMusicPath", song.path);
//                    bundle.putLong("songID", song.id);
//                    bundle.putInt("status", 0);
//                    intentService.putExtra("bundle", bundle);
//                    mContext.startService(intentService);
//                    MsgCache.get().put(Constants.MUSIC_INFO, song);
                }
            }, 100);

        }

    }
}
