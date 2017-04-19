package msgcopy.com.musicdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

/**
 * Created by liang on 2017/4/14.
 */

public class SkinBaseFragment extends Fragment{

    private LayoutInflater mLayoutInflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        LayoutInflater result = getActivity().getLayoutInflater();
        return result;
    }
}
