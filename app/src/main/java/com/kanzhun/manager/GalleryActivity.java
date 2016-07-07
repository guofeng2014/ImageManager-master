package com.kanzhun.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    /**
     * 请求权限
     */
    private static final int REQUEST_READ_EXTERNAL_PERMISSION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initViews();
        if (hasWriteExternalPermission()) {
            initData();
        }
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
            List<String> totalImage = new ArrayList<>();
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File parentFile = new File(path).getParentFile();
                if (parentFile == null || !parentFile.exists()) continue;
                // 添加全部图片
                totalImage.add(path);
            }
            cursor.close();
            //本地没有图片
            if (totalImage.size() <= 0) {
                handler.sendEmptyMessage(LOAD_EMPTY);
                return;
            }

            //所有图片
            FolderBean allBean = new FolderBean();
            List<String> allList = allBean.files;
            folderList.add(allBean);
            allBean.folderName = "所有图片";

            //分组逻辑
            Map<String, FolderBean> group = new HashMap<>();
            for (String s : totalImage) {
                File file = new File(s);
                File parent = file.getParentFile();
                if (!parent.exists()) continue;
                String fonderName = parent.getName();
                //全部图片
                allList.add(s);
                if (!group.containsKey(fonderName)) {
                    FolderBean bean = new FolderBean();
                    bean.coverImagePath = file.toString();
                    bean.folderName = fonderName;
                    bean.files.add(s);
                    group.put(fonderName, bean);
                    folderList.add(bean);
                } else {
                    FolderBean bean = group.get(fonderName);
                    bean.files.add(s);
                }
            }
            int count = allList.size();
            if (count > 0) {
                allBean.coverImagePath = allList.get(0);
                handler.obtainMessage(LOAD_SUCCEED, allBean).sendToTarget();
            }
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
        tvDirName.setText(bean.folderName);
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

    /**
     * permission callback
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_PERMISSION) {
            //permission allow
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            }
            //permission deny
            else {
                Toast.makeText(GalleryActivity.this, "获取相册失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * check permission
     */
    private boolean hasWriteExternalPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_PERMISSION);
            return false;
        }
        return true;
    }
}
