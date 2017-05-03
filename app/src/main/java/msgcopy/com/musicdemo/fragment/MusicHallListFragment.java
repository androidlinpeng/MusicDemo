package msgcopy.com.musicdemo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.DownloadOnlineMusic;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.MusicHallListAdapter;
import msgcopy.com.musicdemo.modul.NewSong;
import msgcopy.com.musicdemo.utils.FileUtils;
import msgcopy.com.musicdemo.utils.ToastUtils;
import rx.Subscriber;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallListFragment extends BaseFragment implements MusicHallListAdapter.OnMoreClickListener{

    private static final String TAG = "MusicHallListFragment";

    @BindView(R.id.recyclerview)
    XRecyclerView recyclerView;
    private MusicHallListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Subscriber<NewSong> subscriberGet;
    List<NewSong.SongListBean> song_list;
    private String type = "";

    private int loadingNumber = 10;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_music_hall_list;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            type = getArguments().getString("type");
        }

        mAdapter = new MusicHallListAdapter((AppCompatActivity) getActivity(), null, "", true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getHttp(type,""+loadingNumber);
                        recyclerView.refreshComplete();
                    }
                },1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingNumber += 10;
                        getHttp(type,""+loadingNumber);
                        recyclerView.loadMoreComplete();
                    }
                },1000);
            }
        });

        mAdapter.setOnMoreClickListener(this);

        getHttp(type,""+loadingNumber);

    }

    public void getHttp(String type,String size) {
        //git请求
        subscriberGet = new Subscriber<NewSong>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted:");
            }

            @Override
            public void onError(Throwable onError) {
                Log.i(TAG, "onError:" + onError.getMessage());
            }

            @Override
            public void onNext(NewSong newSong) {
                Log.i(TAG, "onNext:"+newSong.getBillboard().getName());
                song_list = new ArrayList<NewSong.SongListBean>();
                song_list = newSong.getSong_list();
                mAdapter.setSongList(song_list);
            }
        };
        new HttpUser().getGetData(subscriberGet,type,size);
    }

    @Override
    public void onMoreClick(int position) {
        final NewSong.SongListBean song = song_list.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(song.getTitle());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(song.getArtist_name(), song.getTitle());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 查看歌手信息
                        artistInfo(song);
                        break;
                    case 1:// 下载
                        download(song);
                        break;
                }
            }
        });
        dialog.show();
    }

    private void artistInfo(NewSong.SongListBean song) {

    }

    private void download(final NewSong.SongListBean song) {
        new DownloadOnlineMusic(getActivity(), song) {
            @Override
            public void onPrepare() {
//                mProgressDialog.show();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                ToastUtils.showLong(getActivity(),"正在下载"+song.getTitle());
            }

            @Override
            public void onExecuteFail(Exception e) {
                ToastUtils.showLong(getActivity(),"无法下载");
            }
        }.execute();

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
