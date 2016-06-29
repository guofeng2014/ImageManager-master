package com.kanzhun.manager.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kanzhun.manager.ImageConfig;
import com.kanzhun.manager.bean.ImageInfo;
import com.kanzhun.manager.itfs.OnImageLoadCompleteListener;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageLoader2 implements OnImageLoadCompleteListener {

    /**
     * 当前实利
     */
    private static ImageLoader2 instance;
    /**
     * 配置信息
     */
    private ImageConfig config;
    /**
     * 轮训线程
     */
    private PollingTask task;


    public static ImageLoader2 get() {
        if (instance == null) {
            synchronized (ImageLoader2.class) {
                if (instance == null) {
                    instance = new ImageLoader2();
                }
            }
        }
        return instance;
    }


    public void loadImage(String path, ImageView imageView) {
        if (config == null) throw new NullPointerException("请初始化ImageConfig");
        //加载默认图片
        imageView.setImageResource(config.getDefaultResourceId());
        //设置TAG
        imageView.setTag(path);
        //从缓存获取bitmap对象
        Bitmap b = config.getmLruCache().get(path);
        //打包数据
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.path = path;
        imageInfo.imageView = imageView;
        imageInfo.bitmap = b;
        //直接加载缓存bitmap
        if (b != null) {
            onImageRefresh(imageInfo);
            return;
        }
        //监测轮播线程
        checkRollThread();
        //添加Task任务到解析队列里
        TaskRunnable r = new TaskRunnable(imageInfo, config);
        r.setOnImageLoadCompleteListener(this);
        config.getParserQueue().add(r);
    }

    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ImageInfo imageInfo = (ImageInfo) msg.obj;
            Bitmap b = imageInfo.bitmap;
            ImageView iv = imageInfo.imageView;
            String path = imageInfo.path;
            if (TextUtils.equals(iv.getTag().toString(), path)) {
                //文件加载失败
                if (b == null) {
                    iv.setImageResource(config.getErrorResourceId());
                }
                //文件正常
                else {
                    iv.setImageBitmap(b);
                }
            }
            return true;
        }
    });

    public void release() {
        task.quit();
        config.getmLruCache().clear();
        System.gc();
    }

    @Override
    public void onImageRefresh(ImageInfo imageInfo) {
        Message message = Message.obtain();
        message.obj = imageInfo;
        mUIHandler.sendMessage(message);
    }

    public void init(ImageConfig config) {
        this.config = config;
    }

    /**
     * 坚持轮播线程是否活着
     */
    private void checkRollThread() {
        if (task == null || !task.isAlive()) {
            task = new PollingTask(config.getParserQueue(), config.getQueueType(), config.getThreadPool(), config.getSemaphore(), config.getGridCount());
            task.start();
        }
    }
}
