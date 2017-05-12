package msgcopy.com.musicdemo.fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msgcopy.com.musicdemo.Constants;
import msgcopy.com.musicdemo.MsgCache;
import msgcopy.com.musicdemo.MyApplication;
import msgcopy.com.musicdemo.R;
import msgcopy.com.musicdemo.RxBus;
import msgcopy.com.musicdemo.adapter.SongsListAdapter;
import msgcopy.com.musicdemo.dataloader.SongLoader;
import msgcopy.com.musicdemo.event.MediaUpdateEvent;
import msgcopy.com.musicdemo.modul.Song;
import msgcopy.com.musicdemo.permission.PermissionManager;
import msgcopy.com.musicdemo.permission.PermissionUtils;
import msgcopy.com.musicdemo.utils.FileUtils;
import msgcopy.com.musicdemo.utils.ListenerUtil;
import msgcopy.com.musicdemo.utils.SystemUtils;
import msgcopy.com.musicdemo.utils.ToastUtils;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static msgcopy.com.musicdemo.Constants.MUSIC_LIST;

/**
 * Created by liang on 2017/4/14.
 */

public class SongsFragment extends BaseFragment implements SongsListAdapter.OnMoreClickListener {

    private static final int REQUEST_WRITE_SETTINGS = 1;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private String action;
    private SongsListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Song> musicList;

    private static final int ACTION_REFRESH = 1;
    private static final int ACTION_LOAD_MORE = 2;
    private int mCurrentAction = ACTION_REFRESH;

    public static SongsFragment newInstance(String action) {
        Bundle args = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                args.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        SongsFragment fragment = new SongsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
        super.setUpView(view);
        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
        mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), null, action, true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemListDivider(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnMoreClickListener(this);

        updataMedia();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(getContext())) {
                ToastUtils.showLong(getActivity(),"授权成功，请设置铃声");
            }
        }
    }

    //应用启动时通知系统刷新媒体库,
    private void updataMedia() {
        PermissionManager.init(MyApplication.getInstance());
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_READ_EXTERNAL_STORAGE);
            }
            SongLoader.getAllSongs(getActivity())
                    .map(new Func1<List<Song>, String[]>() {
                        @Override
                        public String[] call(List<Song> songList) {
                            List<String> folderPath = new ArrayList<String>();
                            int i = 0;
                            musicList = new ArrayList<Song>();
                            for (Song song : songList) {
                                folderPath.add(i, song.path);
                                i++;
                                musicList.add(song);
                                Log.i("songList", "" + song.path);
                                Log.i("songList", "" + song.id);
                            }
                            mAdapter.setSongList(musicList);

                            return folderPath.toArray(new String[0]);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String[]>() {
                        @Override
                        public void call(String[] paths) {
                            MediaScannerConnection.scanFile(getContext(), paths, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            if (uri == null) {
                                                RxBus.getInstance().post(new MediaUpdateEvent());
                                            }
                                        }
                                    });
                        }
                    });
        } else {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
        }


    }

    @Override
    public void onMoreClick(int position) {
        final Song song = musicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(song.title);
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(song.artistName, song.title);
        File file = new File(path);
        int itemsId = R.array.local_music_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 查看歌手信息
                        artistInfo(song);
                        break;
                    case 1:// 查看歌手信息
                        musicInfo(song);
                        break;
                    case 2:// 设置铃声
                        requestSetRingtone(song);
                        break;
                    case 3:// 删除
                        deleteSong(song);
                        break;
                }
            }
        });
        dialog.show();
    }

    private void artistInfo(Song song) {
    }

    private void musicInfo(Song song) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(song.title);
        StringBuilder sb = new StringBuilder();
        sb.append("艺术家：")
                .append(song.artistName)
                .append("\n\n")
                .append("专辑：")
                .append(song.albumName)
                .append("\n\n")
                .append("播放时长：")
                .append(SystemUtils.stringForTime(song.duration))
                .append("\n\n")
                .append("文件路径：")
                .append(new File(song.path).getParent());
        dialog.setMessage(sb.toString());
        dialog.show();
    }

    private void requestSetRingtone(final Song song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
            ToastUtils.showLong(getActivity(),"没有权限，无法设置铃声，请授予权限");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
        } else {
            setRingtone(song);
        }
    }

    /**
     * 设置铃声
     */
    private void setRingtone(Song song) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(song.path);
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = getContext().getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{song.path}, null);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_PODCAST, false);

            getContext().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",new String[]{song.path});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            ToastUtils.showLong(getActivity(),R.string.setting_ringtone_success);
        }
        cursor.close();
    }

    private void deleteSong(final Song song) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = song.title;
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.getInstance().getMusicService().mlist.remove(song);
                File file = new File(song.path);
                if (file.delete()) {
                    MyApplication.getInstance().getMusicService().updatePlayingPosition();
                    updateView(song);
                    // 刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + song.path));
                    getContext().sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();

    }

    private void updateView(Song song) {
        musicList.remove(song);
        mAdapter.notifyDataSetChanged();
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

    public static List<Song> getMusicList() {
        return (List<Song>) MsgCache.get().getAsObject(MUSIC_LIST);
    }
}
