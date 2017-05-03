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
    private List<SongList.ContentBeanX> mlist;
    private Subscriber<SongList> subscriberGet;
//    private List<MusicHallTop> mTiles = new ArrayList<MusicHallTop>();

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_music_hall;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);

//        initData();

        getHttp();

    }

//    private void initData() {
//
//        MusicHallTop hallTop = new MusicHallTop("新歌榜", 1);
//        MusicHallTop hallTop1 = new MusicHallTop("热歌榜", 2);
//        MusicHallTop hallTop2 = new MusicHallTop("欧美金曲榜", 21);
//        MusicHallTop hallTop3 = new MusicHallTop("King榜", 100);
//        MusicHallTop hallTop4 = new MusicHallTop("原创音乐榜", 200);
//        MusicHallTop hallTop5 = new MusicHallTop("华语金曲榜", 20);
//        MusicHallTop hallTop6 = new MusicHallTop("经典老歌榜", 22);
//        MusicHallTop hallTop7 = new MusicHallTop("网络歌曲榜", 25);
//        MusicHallTop hallTop8 = new MusicHallTop("影视金曲榜", 24);
//        MusicHallTop hallTop9 = new MusicHallTop("情歌对唱榜", 23);
//        mTiles.add(hallTop);
//        mTiles.add(hallTop1);
//        mTiles.add(hallTop2);
//        mTiles.add(hallTop3);
//        mTiles.add(hallTop4);
//        mTiles.add(hallTop5);
//        mTiles.add(hallTop6);
//        mTiles.add(hallTop7);
//        mTiles.add(hallTop8);
//        mTiles.add(hallTop9);
//
//        mAdapter = new MusicHallPagerAdapter(getChildFragmentManager(), mTiles);
//        for (int i = 0; i < mTiles.size(); i++) {
//            mTabLayout.addTab(mTabLayout.newTab().setText(mTiles.get(i).getName()));
//        }
//        if (mAdapter != null) {
//            mViewPager.setAdapter(mAdapter);
//            mViewPager.setOffscreenPageLimit(3);
//            mTabLayout.setupWithViewPager(mViewPager);
//        }
//    }


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
                    mViewPager.setOffscreenPageLimit(1);
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
