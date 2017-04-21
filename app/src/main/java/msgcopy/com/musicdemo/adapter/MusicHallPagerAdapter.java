package msgcopy.com.musicdemo.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import msgcopy.com.musicdemo.fragment.MusicHallListFragment;
import msgcopy.com.musicdemo.utils.ViewUtils;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallPagerAdapter extends FragmentStatePagerAdapter {

    private static String TAG = "MusicHallPagerAdapter";
    private static String[] mTitles;

    public MusicHallPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ViewUtils.createFragment(MusicHallListFragment.class, false);
        Bundle bundle = new Bundle();
        bundle.putString("type", mTitles[position]);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }
}