package msgcopy.com.musicdemo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MainActivity;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.dataloader.AlbumSongLoader;
import msgcopy.com.musicdemo.fragment.BaseFragment;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;
import msgcopy.com.musicdemo.view.DividerItemDecoration;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liang on 2017/4/25.
 */

public class AlbumDetailFragment extends BaseFragment {

    @BindView(R.id.mToolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fab_play)
    FloatingActionButton fabPlay;
    @BindView(R.id.album_art)
    ImageView albumArt;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private PreferencesUtility mPreferences;
    private Context context;
    private AlbumSongsAdapter mAdapter;
    private long albumID = -1;
    private String albumName;
    private int primaryColor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(Constants.ALBUM_ID);
            albumName = getArguments().getString(Constants.ALBUM_NAME);
        }
        context = getActivity();
        mPreferences = PreferencesUtility.getInstance(context);
        mAdapter = new AlbumSongsAdapter(getActivity(), albumID);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_album_detail;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getArguments().getBoolean("transition")) {
            albumArt.setTransitionName(getArguments().getString("transition_name"));
            albumArt.setBackgroundResource(R.drawable.icon_album_default);
        }

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false));

        setupToolbar();
        loadAlbumArt(albumID);
        subscribeMetaChangedEvent();


    }

    private void subscribeMetaChangedEvent() {
        AlbumSongLoader.getSongsForAlbum(context, albumID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        List<String> folderPath = new ArrayList<String>();
                        List<Song> musicList = new ArrayList<Song>();
                        for (Song song : songs) {
                            musicList.add(song);
                        }
                        mAdapter.setSongList(musicList);
                    }
                });
    }

    public void loadAlbumArt(long albumID) {
        Glide.with(getActivity())
                .load(ListenerUtil.getAlbumArtUri(albumID))
                .asBitmap()
                .priority(Priority.IMMEDIATE)
                .error(R.drawable.icon_album_default)
                .into(albumArt);
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(albumName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
                MainActivity.mToolbar.setVisibility(View.VISIBLE);
            }
        });
    }

}
