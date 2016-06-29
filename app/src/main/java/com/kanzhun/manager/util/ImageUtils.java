package com.kanzhun.manager.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kanzhun.manager.bean.ImageSizeInfo;

import java.lang.reflect.Field;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageUtils {

    public static Bitmap decodeSampledBitmapFromPath(Bitmap.Config config, String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 获取图片的宽高但是不把图片加载到内存中
        options.inJustDecodeBounds = true;
        //加载图片信息
        BitmapFactory.decodeFile(path, options);
        //计算缩放比
        options.inSampleSize = calculateInSampleSize(options, width, height);
        //图片质量
        options.inPreferredConfig = config;
        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRatio = Math.round(width * 1.0f / reqWidth);
            int heightRatio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    public static ImageSizeInfo getImageViewSize(ImageView imageView) {
        ImageSizeInfo imageSizeInfo = new ImageSizeInfo();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            DisplayMetrics dm = imageView.getContext().getResources().getDisplayMetrics();
            width = dm.widthPixels;
        }
        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            DisplayMetrics dm = imageView.getContext().getResources().getDisplayMetrics();
            height = dm.heightPixels;
        }
        imageSizeInfo.width = width;
        imageSizeInfo.height = height;
        return imageSizeInfo;
    }

    /**
     * 通过反射获取图片ImageView的某个属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        Field field;
        try {
            field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
}
