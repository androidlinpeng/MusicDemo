package msgcopy.com.musicdemo.fragment;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MainActivity;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.ArtistSongAdapter;
import msgcopy.com.musicdemo.dataloader.ArtistSongLoader;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.utils.LogUtil;
import msgcopy.com.musicdemo.view.DividerItemDecoration;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liang on 2017/4/26.
 */

public class ArtistDetailFragment extends BaseFragment {

    @BindView(R.id.artist_art)
    ImageView artistArt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fab_play)
    FloatingActionButton fabPlay;
    @BindView(R.id.recycler_view_songs)
    RecyclerView recyclerviewsongs;

    private Context context;
    private long artistID = -1;
    private String artistName = "";
    private int primaryColor;

    private ArtistSongAdapter mSongAdapter;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_artist_detail;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
            artistName = getArguments().getString(Constants.ARTIST_NAME);
        }
        context = getActivity();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getArguments().getBoolean("transition")) {
            artistArt.setTransitionName(getArguments().getString("transition_name"));
        }

        recyclerviewsongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerviewsongs.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, true));
        mSongAdapter = new ArtistSongAdapter(getActivity(), null, artistID);
        recyclerviewsongs.setAdapter(mSongAdapter);

        setupToolbar();

        subscribeMetaChangedEvent();

    }

    private void subscribeMetaChangedEvent() {

        ArtistSongLoader.getSongsForArtist(context,artistID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        List<Song> musicList = new ArrayList<Song>();
                        for (Song song : songs) {
                            musicList.add(song);
                        }
                        musicList.add(0, new Song(-1, -1, -1, "dummy", "dummy", "dummy", -1, -1));
                        LogUtil.i("ArtistSongLoader",""+musicList.get(0).title);
                        mSongAdapter.setSongList(musicList);
                    }
                });
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(artistName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
                MainActivity.mToolbar.setVisibility(View.VISIBLE);
            }
        });
    }

}
