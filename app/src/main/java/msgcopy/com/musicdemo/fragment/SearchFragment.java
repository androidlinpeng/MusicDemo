package msgcopy.com.musicdemo.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.MainActivity;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.SearchListAdapter;
import msgcopy.com.musicdemo.dataloader.SongLoader;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.modul.online.SongSearch;
import msgcopy.com.musicdemo.utils.PreferencesUtility;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liang on 2017/4/21.
 */

public class SearchFragment extends BaseFragment implements SearchView.OnQueryTextListener, View.OnTouchListener {

    private static final String TAG = "SearchFragment";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    //    @BindView(R.id.toolbar)
//    Toolbar mToolbar;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.view_empty)
    RelativeLayout view_empty;
    private SearchListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String action = Constants.PLAYLIST_TYPE;

    private SearchView mSearchView;
    private String queryString;
    private InputMethodManager mImm;

    private PreferencesUtility mPreferences;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_search;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);
        mPreferences = PreferencesUtility.getInstance(getActivity());

        mAdapter = new SearchListAdapter((AppCompatActivity) getActivity(), null, action, true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @OnClick({R.id.search, R.id.back})
    public void onClick(ImageView view) {
        switch (view.getId()) {
            case R.id.search:
                String query = input.getText().toString();
                if (mPreferences.getStartMusicMode().equals(Constants.LOCAL_MUSIC)) {
                    updataMedia(query);
                } else {
                    getSongSearch(query);
                }

                break;
            case R.id.back:
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
                MainActivity.mToolbar.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void getSongSearch(String query) {
        Subscriber<SongSearch> subscriberSearch = new Subscriber<SongSearch>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted:");
            }

            @Override
            public void onError(Throwable onError) {
                Log.i(TAG, "onError:" + onError.getMessage());
                recyclerView.setVisibility(View.GONE);
                view_empty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(SongSearch songSearch) {
                Log.i(TAG, "onNext:");
                if (songSearch.getSong().size() > 0) {
                    List<Song> songs = new ArrayList<Song>();
                    for (int i = 0; i < songSearch.getSong().size(); i++) {
                        Song song = new Song(Constants.ONLINE_MUSIC,Long.parseLong(songSearch.getSong().get(i).getSongid()), -1, -1, songSearch.getSong().get(i).getSongname(), songSearch.getSong().get(i).getArtistname(), "", -1, -1);
                        songs.add(song);
                    }
                    mAdapter.setSongList(songs);
                    recyclerView.setVisibility(View.VISIBLE);
                    view_empty.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    view_empty.setVisibility(View.VISIBLE);
                }

            }
        };
        new HttpUser().getSongSearch(subscriberSearch, query);
    }

    private void updataMedia(String string) {
        SongLoader.searchSongs(getActivity(), string)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        if (songs.size() > 0) {
                            mAdapter.setSongList(songs);
                            recyclerView.setVisibility(View.VISIBLE);
                            view_empty.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            view_empty.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


    private static class ItemListDivider extends RecyclerView.ItemDecoration {

        private Drawable drawable = null;

        public ItemListDivider(Context cxt) {
            this.drawable = cxt.getResources().getDrawable(R.drawable.divider_article_list);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + drawable.getIntrinsicHeight();

                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }
    }

}
