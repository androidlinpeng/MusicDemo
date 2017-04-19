package msgcopy.com.musicdemo.fragment;


import msgcopy.com.musicdemo.R;

/**
 * Created by liang on 2017/4/14.
 */

public class AlbumFragment extends BaseFragment {

    public static AlbumFragment newInstance(String action) {
        AlbumFragment fragment = new AlbumFragment();
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_recyclerview;
    }
}
