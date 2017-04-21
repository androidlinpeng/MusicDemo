package msgcopy.com.musicdemo.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.SearchListAdapter;
import msgcopy.com.musicdemo.dataloader.SongLoader;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.permission.PermissionManager;
import msgcopy.com.musicdemo.permission.PermissionUtils;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static msgcopy.com.musicdemo.R.id.recyclerview;

/**
 * Created by liang on 2017/4/21.
 */

public class SearchFragment extends BaseFragment {

    @BindView(recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.view_empty)
    RelativeLayout view_empty;
    private SearchListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String action = Constants.PLAYLIST_TYPE;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_search;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);

        mAdapter = new SearchListAdapter((AppCompatActivity) getActivity(), null, action, true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);

    }

    @OnClick(R.id.search)
    public void onClick(ImageView view){
        String string = input.getText().toString();
        updataMedia(string);
    }


    //应用启动时通知系统刷新媒体库,
    private void updataMedia(String string) {
        PermissionManager.init(MyApplication.getInstance());
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
            }
            SongLoader.searchSongs(getActivity(), string)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Song>>() {
                        @Override
                        public void call(List<Song> songs) {
                            if (songs.size()>0) {
                                mAdapter.setSongList(songs);
                                recyclerView.setVisibility(View.VISIBLE);
                                view_empty.setVisibility(View.GONE);
                            }else {
                                recyclerView.setVisibility(View.GONE);
                                view_empty.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
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
