package msgcopy.com.musicdemo.fragment;

import android.Manifest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.adapter.ArtistAdapter;
import msgcopy.com.musicdemo.dataloader.ArtistLoader;
import msgcopy.com.musicdemo.event.MediaUpdateEvent;
import msgcopy.com.musicdemo.modul.Artist;
import msgcopy.com.musicdemo.permission.PermissionManager;
import msgcopy.com.musicdemo.permission.PermissionUtils;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.PreferencesUtility;
import msgcopy.com.musicdemo.view.DividerItemDecoration;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liang on 2017/4/14.
 */

public class ArtistFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private RecyclerView.ItemDecoration itemDecoration;
    private GridLayoutManager layoutManager;
    private PreferencesUtility mPreferences;
    private boolean isGrid;
    private String action;
    private ArtistAdapter mAdapter;

    public static ArtistFragment newInstance(String action) {
        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        isGrid = mPreferences.isArtistsInGrid();
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
        }

        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
        mAdapter = new ArtistAdapter(getActivity(), action);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);
        setItemDecoration();

        updataMedia();

    }

    //应用启动时通知系统刷新媒体库,
    private void updataMedia() {
        PermissionManager.init(MyApplication.getInstance());
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                return;
//            }
            if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
            }
            ArtistLoader.getAllArtists(getActivity())
                    .map(new Func1<List<Artist>, String[]>() {
                        @Override
                        public String[] call(List<Artist> artistList) {
                            List<String> folderPath = new ArrayList<String>();
                            int i = 0;
                            List<Artist> musicList = new ArrayList<Artist>();
                            for (Artist artist : artistList) {
                                musicList.add(artist);
                                Log.i("artistList", "" + artistList.size());
                            }
                            mAdapter.setArtistList(musicList);
                            return folderPath.toArray(new String[0]);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String[]>() {
                        @Override
                        public void call(String[] paths) {
                            MediaScannerConnection.scanFile(getContext(), paths, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            if (uri == null) {
                                                RxBus.getInstance().post(new MediaUpdateEvent());
                                            }
                                        }
                                    });
                        }
                    });
        } else {

        }

    }

    private void setItemDecoration() {
        if (isGrid) {
            int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
            itemDecoration = new SpacesItemDecoration(spacingInPixels);
        } else {
            itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        mAdapter = new ArtistAdapter(getActivity(), action);
        recyclerView.setAdapter(mAdapter);
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
//        mPresenter.loadArtists(action);
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position % 2 == 0) {
                outRect.left = 0;
                outRect.top = space;
                outRect.right = space / 2;
            } else {
                outRect.left = space / 2;
                outRect.top = space;
                outRect.right = 0;
            }
        }
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







