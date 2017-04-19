package msgcopy.com.musicdemo.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.R;

/**
 * Created by liang on 2017/4/14.
 */

public class MainFragment extends BaseFragment {

    public static final String NAVIGATE_ALLSONG = "navigate_all_song";

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
        tabLayout.setupWithViewPager(viewPager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_main;
    }


    private void setupViewPager(ViewPager viewPager) {
        String action = NAVIGATE_ALLSONG;
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(SongsFragment.newInstance(action), this.getString(R.string.songs));
        adapter.addFragment(ArtistFragment.newInstance(action), this.getString(R.string.artists));
        adapter.addFragment(AlbumFragment.newInstance(action), this.getString(R.string.albums));
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}






