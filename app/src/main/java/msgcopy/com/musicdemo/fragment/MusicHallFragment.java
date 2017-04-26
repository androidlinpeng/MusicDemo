package msgcopy.com.musicdemo.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.HttpUser;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.MusicHallPagerAdapter;
import msgcopy.com.musicdemo.modul.SongList;
import msgcopy.com.musicdemo.utils.CommonUtil;
import rx.Subscriber;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallFragment extends BaseFragment {

    private static final String TAG = "MusicHallFragment";

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;
    MusicHallPagerAdapter mAdapter;
//    String[] mTitles;
    private List<SongList.ContentBeanX> mlist;

    private Subscriber<SongList> subscriberGet;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_music_hall;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);

        getHttp();

    }

    public void getHttp() {
        //git请求
        subscriberGet = new Subscriber<SongList>() {
            @Override
            public void onCompleted() {
//                Toast.makeText(getActivity(), "请求成功", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onCompleted:");
            }

            @Override
            public void onError(Throwable onError) {
                Log.i(TAG, "onError:" + onError.getMessage());
            }

            @Override
            public void onNext(SongList songList) {
                Log.i(TAG, "onNext:"+songList.getContent().get(0).getName());

                mlist = new ArrayList<SongList.ContentBeanX>();

                for (int i = 0; i < songList.getContent().size(); i++) {
                    if (CommonUtil.isBlank(songList.getContent().get(i).getWeb_url())){
                        mlist.add(songList.getContent().get(i));
                        Log.i(TAG, "onNext:"+songList.getContent().get(i).getName());
                    }
                }

                mAdapter = new MusicHallPagerAdapter(getChildFragmentManager(), mlist);

                for (int i = 0; i < mlist.size(); i++) {
                    mTabLayout.addTab(mTabLayout.newTab().setText(mlist.get(i).getName()));
                    Log.i(TAG, "onNext:mlist"+mlist.get(i).getName());
                }

                if (mAdapter != null) {
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.setOffscreenPageLimit(3);
                    mTabLayout.setupWithViewPager(mViewPager);
                }
            }
        };
        new HttpUser().getSongListData(subscriberGet);
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new MusicHallPagerAdapter(getChildFragmentManager(), mlist);
    }
}
