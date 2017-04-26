package msgcopy.com.musicdemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.View;

import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MainActivity;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.adapter.AlbumDetailFragment;
import msgcopy.com.musicdemo.fragment.ArtistDetailFragment;

import static msgcopy.com.musicdemo.MainActivity.mCurrentFragment;
import static msgcopy.com.musicdemo.utils.ListenerUtil.isLollipop;

/**
 * Created by liang on 2017/4/25.
 */

public class NavigationUtil {


    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, String albumName, Pair<View, String> transitionViews) {

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment = ViewUtils.createFragment(AlbumDetailFragment.class,false);

        if (isLollipop() && transitionViews != null) {
            Transition changeImage = TransitionInflater.from(context).inflateTransition(R.transition.image_transform);
            transaction.addSharedElement(transitionViews.first, transitionViews.second);
            Bundle args = new Bundle();
            args.putLong(Constants.ALBUM_ID, albumID);
            args.putString(Constants.ALBUM_NAME, albumName);
            args.putBoolean("transition", true);
            args.putString("transition_name", transitionViews.second);
            fragment.setArguments(args);
            fragment.setSharedElementEnterTransition(changeImage);
            fragment.setSharedElementReturnTransition(changeImage);
        } else {
            transaction.setCustomAnimations(R.anim.activity_fade_in, R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
            Bundle args = new Bundle();
            args.putLong(Constants.ALBUM_ID, albumID);
            args.putString(Constants.ALBUM_NAME, albumName);
            args.putBoolean("transition", false);
            args.putString("transition_name", null);
            fragment.setArguments(args);
        }

        MainActivity.mToolbar.setVisibility(View.GONE);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.hide(mCurrentFragment).add(R.id.frame_content, fragment).addToBackStack(null).commit();
        mCurrentFragment = fragment;
    }

    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID, String name, Pair<View, String> transitionViews) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment = ViewUtils.createFragment(ArtistDetailFragment.class,false);

        if (ListenerUtil.isLollipop() && transitionViews != null) {
            Transition changeImage = TransitionInflater.from(context).inflateTransition(R.transition.image_transform);
            transaction.addSharedElement(transitionViews.first, transitionViews.second);
            Bundle args = new Bundle();
            args.putLong(Constants.ARTIST_ID, artistID);
            args.putString(Constants.ARTIST_NAME, name);
            args.putBoolean("transition", true);
            args.putString("transition_name", transitionViews.second);
            fragment.setArguments(args);
            fragment.setSharedElementEnterTransition(changeImage);
            fragment.setSharedElementReturnTransition(changeImage);
        } else {
            transaction.setCustomAnimations(R.anim.activity_fade_in,R.anim.activity_fade_out, R.anim.activity_fade_in, R.anim.activity_fade_out);
            Bundle args = new Bundle();
            args.putLong(Constants.ARTIST_ID, artistID);
            args.putString(Constants.ARTIST_NAME, name);
            args.putBoolean("transition", false);
            args.putString("transition_name", null);
            fragment.setArguments(args);
        }

        MainActivity.mToolbar.setVisibility(View.GONE);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.hide(mCurrentFragment).add(R.id.frame_content, fragment).addToBackStack(null).commit();
        mCurrentFragment = fragment;

    }

}
