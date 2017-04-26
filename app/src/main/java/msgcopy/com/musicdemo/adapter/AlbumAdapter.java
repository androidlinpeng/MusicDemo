package msgcopy.com.musicdemo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import msgcopy.com.musicdemo.utils.NavigationUtil;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Album;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;

/**
 * Created by liang on 2017/3/24.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder>{

    private List<Album> arraylist;
    private Activity mContext;
    private boolean isGrid;
    private String action;

    public AlbumAdapter(Activity context, String action) {
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isAlbumsInGrid();
        this.action = action;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_grid_layout_item, viewGroup, false);
            return new ItemHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_linear_layout_item, viewGroup, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, final int i) {
        Album localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        itemHolder.songcount.setText(ListenerUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount));

        Glide.with(itemHolder.itemView.getContext())
                .load(ListenerUtil.getAlbumArtUri(localItem.id).toString())
                .asBitmap()
                .placeholder(R.drawable.icon_album_default)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(itemHolder.albumArt);

        if (ListenerUtil.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);

        setOnPopupMenuListener(itemHolder, i);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void setAlbumsList(List<Album> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }

    private void setOnPopupMenuListener(final AlbumAdapter.ItemHolder itemHolder, final int position) {

    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private TextView songcount;
        private ImageView albumArt;
        private ImageView popupMenu;
        private View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.text_item_title);
            this.artist = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.songcount = (TextView) view.findViewById(R.id.text_item_subtitle_2);
            this.albumArt = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtil.navigateToAlbum(mContext, arraylist.get(getAdapterPosition()).id,
                    arraylist.get(getAdapterPosition()).title,new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
        }
    }
}