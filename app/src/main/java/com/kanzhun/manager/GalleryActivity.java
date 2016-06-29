package com.kanzhun.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kanzhun.manager.adapter.ImagesAdapter;
import com.kanzhun.manager.bean.FolderBean;
import com.kanzhun.manager.dialog.GallerySelectDialog;
import com.kanzhun.manager.util.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyou on 2016/6/23.
 */
public class GalleryActivity extends Activity implements View.OnClickListener, GallerySelectDialog.IOnDirectorySelectListener {

    private GridView gv;
    private TextView tvDirName;
    private TextView tvDirCount;


    private List<FolderBean> folderList = new ArrayList<>();

    private ProgressDialog pd;

    private ImagesAdapter adapter;
    /**
     * 加载相册数据为空
     */
    private final static int LOAD_EMPTY = 0;
    /**
     * 加载相册有数据
     */
    private final static int LOAD_SUCCEED = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initViews();
        initData();
    }

    private void initViews() {
        gv = (GridView) findViewById(R.id.gv);
        findViewById(R.id.rl_select_bar).setOnClickListener(this);
        tvDirName = (TextView) findViewById(R.id.tv_dir_name);
        tvDirCount = (TextView) findViewById(R.id.tv_dir_count);
    }

    /**
     * 利用ContentProvider扫描本地图片
     */
    private void initData() {
        if (!TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        pd = ProgressDialog.show(this, null, "正在扫描中");

        Thread thread = new Thread(runnable);
        thread.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_bar:
                GallerySelectDialog d = new GallerySelectDialog(this, folderList);
                d.setOnDirectorySelectListener(this);
                d.show();
                break;
            default:
                break;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(uri,
                    null,
                    MediaStore.Images.Media.MIME_TYPE + " =? or " +
                            MediaStore.Images.Media.MIME_TYPE + " =? or " +
                            MediaStore.Images.Media.MIME_TYPE + " =?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_MODIFIED);
            if (cursor == null) return;
            FolderBean allBean = new FolderBean();
            folderList.add(allBean);
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File parentFile = new File(path).getParentFile();
                if (parentFile == null || !parentFile.exists()) continue;
                // 添加全部图片
                allBean.files.add(path);
            }
            cursor.close();
            allBean.setId(-1);
            List<String> allList = allBean.files;
            //本地没有图片
            if (allList.size() <= 0) {
                handler.sendEmptyMessage(LOAD_EMPTY);
                return;
            }
            //第一张照片设置为封面
            allBean.firstImagePath = allList.get(0);
            //分组逻辑
            Map<String, FolderBean> group = new HashMap<>();
            for (String s : allList) {
                File file = new File(s);
                File parent = file.getParentFile();
                if (!parent.exists()) continue;
                if (!group.containsKey(parent.toString())) {
                    FolderBean bean = new FolderBean();
                    bean.firstImagePath = file.toString();
                    bean.setDir(parent.toString());
                    bean.files.add(s);
                    group.put(parent.toString(), bean);
                    folderList.add(bean);
                } else {
                    FolderBean bean = group.get(parent.toString());
                    bean.files.add(s);
                }
            }
            handler.obtainMessage(LOAD_SUCCEED, allBean).sendToTarget();
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what) {
                case LOAD_EMPTY:
                    Toast.makeText(getApplicationContext(), "没有找到可用照片", Toast.LENGTH_SHORT).show();
                    break;
                case LOAD_SUCCEED:
                    FolderBean bean = (FolderBean) msg.obj;
                    refreshAdapter(bean);
                    break;
            }
            return true;
        }
    });

    private void refreshAdapter(FolderBean bean) {
        if (adapter == null) {
            adapter = new ImagesAdapter(this, bean.files);
            gv.setAdapter(adapter);
        } else {
            adapter.setData(bean.files);
            adapter.notifyDataSetChanged();
        }
        tvDirName.setText(bean.id < 0 ? "所有图片" : bean.files.size() + "");
        tvDirCount.setText(bean.files.size() + "");

    }

    @Override
    public void onDirectorySelectAction(FolderBean bean) {
        refreshAdapter(bean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.get().release();
    }


}
