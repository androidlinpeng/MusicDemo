package msgcopy.com.musicdemo.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.adapter.SongsListAdapter;
import msgcopy.com.musicdemo.dataloader.SongLoader;
import msgcopy.com.musicdemo.event.MediaUpdateEvent;
import msgcopy.com.musicdemo.modul.Song;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static msgcopy.com.musicdemo.Constants.MUSIC_LIST;

/**
 * Created by liang on 2017/4/14.
 */

public class SongsFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private String action ;
    private SongsListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    public static SongsFragment newInstance(String action) {
        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        SongsFragment fragment = new SongsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
        super.setUpView(view);
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
        mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), null, action, true);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);

        updataMedia();

    }

    //应用启动时通知系统刷新媒体库,
    private void updataMedia() {
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SongLoader.getAllSongs(getActivity())
                    .map(new Func1<List<Song>, String[]>() {
                        @Override
                        public String[] call(List<Song> songList) {
                            List<String> folderPath = new ArrayList<String>();
                            int i = 0;
                            List<Song> musicList = new ArrayList<Song>();
                            for (Song song : songList) {
                                folderPath.add(i, song.path);
                                i++;
                                musicList.add(song);
                                Log.i("songList",""+song.path);
                            }
                            mAdapter.setSongList(musicList);
                            MsgCache.get().put(Constants.MUSIC_LIST, musicList);
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
    private static class ItemListDivider extends RecyclerView.ItemDecoration{

        private Drawable drawable=null;

        public ItemListDivider(Context cxt){
            this.drawable=cxt.getResources().getDrawable(R.drawable.divider_article_list);
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
    public static List<Song> getMusicList() {
        return (List<Song>) MsgCache.get().getAsObject(MUSIC_LIST);
    }
}











