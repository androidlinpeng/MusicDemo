package msgcopy.com.musicdemo.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.MusicHallListAdapter;
import msgcopy.com.musicdemo.modul.NewSong;
import rx.Subscriber;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallListFragment extends BaseFragment {

    private static final String TAG = "MusicHallListFragment";

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private MusicHallListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    private Subscriber<NewSong> subscriberGet;
    private String type = "";

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

        getHttp(type,""+20);

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
                List<NewSong.SongListBean> song_list = new ArrayList<NewSong.SongListBean>();
                song_list = newSong.getSong_list();
                mAdapter.setSongList(song_list);
            }
        };
        new HttpUser().getGetData(subscriberGet,type,size);
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
