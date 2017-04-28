package msgcopy.com.musicdemo.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import msgcopy.com.musicdemo.fragment.MusicHallListFragment;
import msgcopy.com.musicdemo.modul.online.MusicHallTop;
import msgcopy.com.musicdemo.utils.ViewUtils;

/**
 * Created by liang on 2017/4/21.
 */

public class MusicHallPagerAdapter extends FragmentStatePagerAdapter {

    private static String TAG = "MusicHallPagerAdapter";
//    private static String[] mTitles;
//    private List<SongList.ContentBeanX> mlist;
    private List<MusicHallTop> mlist;

    public MusicHallPagerAdapter(FragmentManager fm, List<MusicHallTop> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ViewUtils.createFragment(MusicHallListFragment.class, false);
        Bundle bundle = new Bundle();
        bundle.putString("type", ""+mlist.get(position).getType());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mlist.get(position).getName();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }
}