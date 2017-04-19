package msgcopy.com.musicdemo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.List;

import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.modul.Artist;
import msgcopy.com.musicdemo.modul.ArtistArt;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;

/**
 * Created by liang on 2017/3/24.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder>{

    private List<Artist> arraylist;
    private Activity mContext;
    private boolean isGrid;
    private String action;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
    }

    public ArtistAdapter(Activity context, String action) {
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
        this.action = action;
    }


    public void setArtistList(List<Artist> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
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
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Artist localItem = arraylist.get(i);

        itemHolder.name.setText(localItem.name);
        itemHolder.albumCount.setText(ListenerUtil.makeLabel(mContext, R.plurals.Nalbums, localItem.albumCount));
        itemHolder.songCount.setText(ListenerUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount));

        String artistArtJson = PreferencesUtility.getInstance(mContext).getArtistArt(localItem.id);
        if (TextUtils.isEmpty(artistArtJson)) {
//            ArtistArt artistArt = new Gson().fromJson(artistArtJson, ArtistArt.class);
//            loadArtistArt(artistArt, itemHolder);
        }else {
            ArtistArt artistArt = new Gson().fromJson(artistArtJson, ArtistArt.class);
            loadArtistArt(artistArt, itemHolder);
        }

        if (ListenerUtil.isLollipop())
            itemHolder.artistImage.setTransitionName("transition_artist_art" + i);

        setOnPopupMenuListener(itemHolder, i);

    }

    private void loadArtistArt(ArtistArt artistArt, final ItemHolder itemHolder) {
        if (isGrid) {
            Glide.with(mContext)
                    .load(artistArt.getExtralarge())
                    .asBitmap()
                    .placeholder(R.drawable.icon_album_default)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(itemHolder.artistImage);
        }else {
            Glide.with(mContext)
                    .load(artistArt.getLarge())
                    .placeholder(R.drawable.icon_album_default)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.icon_album_default)
                    .into(itemHolder.artistImage);
        }
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void updateDataSet(List<Artist> arrayList) {
        this.arraylist = arrayList;
    }

    private void setOnPopupMenuListener(final ArtistAdapter.ItemHolder itemHolder, final int position) {

    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView albumCount;
        private TextView songCount;
        private ImageView artistImage;
        private ImageView popupMenu;
        private View footer;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.text_item_title);
            this.albumCount = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.songCount = (TextView) view.findViewById(R.id.text_item_subtitle_2);
            this.artistImage = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}