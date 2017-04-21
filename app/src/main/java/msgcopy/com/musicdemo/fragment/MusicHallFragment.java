package msgcopy.com.musicdemo.fragment;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.MusicHallPagerAdapter;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallFragment extends BaseFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;
    MusicHallPagerAdapter mAdapter;
    String[] mTitles;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_music_hall;
    }

    @Override
    protected void setUpView(View view) {
        super.setUpView(view);
        ButterKnife.bind(this, view);

        mTitles = new String[]{"华语歌曲", "日韩歌曲", "欧美歌曲", "KTV热歌", "网络歌曲", "流行歌曲", "快乐歌曲", "运动歌曲"};
        mAdapter = new MusicHallPagerAdapter(getChildFragmentManager(), mTitles);

        for (int i = 0; i < mTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]));
        }

        new SetAdapterTask().execute();

    }
    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mAdapter != null) {
                mViewPager.setAdapter(mAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new MusicHallPagerAdapter(getChildFragmentManager(), mTitles);
    }
}
