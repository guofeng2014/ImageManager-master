package com.kanzhun.manager.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kanzhun.manager.R;
import com.kanzhun.manager.util.ImageLoader;

import java.util.List;

/**
 * Created by zhouyou on 2016/6/23.
 */
public class ImagesAdapter extends BaseAdapter {

    private List<String> data;
    private LayoutInflater inflater;

    private int height;

    public ImagesAdapter(Context context, List<String> data) {
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        height = dm.widthPixels / 3;
    }

    public void setData(List<String> list) {
        this.data = list;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public String getItem(int position) {
        if (data == null || position >= data.size()) {
            return "";
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_image, null);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
            ViewGroup.LayoutParams lp = holder.ivImage.getLayoutParams();
            lp.height = height;
            holder.ivImage.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = getItem(position);
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.get().loadImage(path, holder.ivImage);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
    }
}
