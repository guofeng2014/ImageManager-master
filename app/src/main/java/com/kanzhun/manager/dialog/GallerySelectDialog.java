package com.kanzhun.manager.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kanzhun.manager.R;
import com.kanzhun.manager.bean.FolderBean;
import com.kanzhun.manager.util.ImageLoader;
import com.tandong.bottomview.view.BottomView;

import java.util.List;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class GallerySelectDialog implements AdapterView.OnItemClickListener {

    private Context context;
    private List<FolderBean> folderList;
    private BottomView bottomView;
    private IOnDirectorySelectListener listener;

    public GallerySelectDialog(Context context, List<FolderBean> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    public void setOnDirectorySelectListener(IOnDirectorySelectListener listener) {
        this.listener = listener;
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_gallery_dialog, null);
        ListView lv = (ListView) view.findViewById(R.id.lv);
        bottomView = new BottomView(context, R.style.BottomViewTheme_Transparent, view);
        bottomView.setAnimation(R.style.BottomToTopAnim);

        GallerySelectAdapter adapter = new GallerySelectAdapter(context, folderList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        if (context != null) {
            bottomView.showBottomView(true);
        }
    }


    public void dismiss() {
        if (bottomView != null) {
            bottomView.dismissBottomView();
            bottomView = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (listener == null) return;
        ListView lv = (ListView) parent;
        FolderBean item = (FolderBean) lv.getItemAtPosition(position);
        if (item == null) return;
        listener.onDirectorySelectAction(item);
    }

    private static class GallerySelectAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<FolderBean> data;

        public GallerySelectAdapter(Context context, List<FolderBean> data) {
            this.data = data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (data == null) {
                return 0;
            }
            return data.size();
        }

        @Override
        public FolderBean getItem(int position) {
            if (data == null || position >= data.size()) {
                return null;
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
                convertView = inflater.inflate(R.layout.item_dir, null);
                holder.ivDir = (ImageView) convertView.findViewById(R.id.iv_dir);
                holder.tvDirName = (TextView) convertView.findViewById(R.id.tv_dir_name);
                holder.tvDirCount = (TextView) convertView.findViewById(R.id.tv_dir_count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FolderBean item = getItem(position);
            if (item != null) {
                holder.tvDirName.setText(item.folderName);
                holder.tvDirCount.setText(item.files.size() + "");
                ImageLoader.get().loadImage(item.firstImagePath, holder.ivDir);
            }
            return convertView;
        }

        static class ViewHolder {
            ImageView ivDir;
            TextView tvDirName;
            TextView tvDirCount;
        }
    }

    public interface IOnDirectorySelectListener {
        void onDirectorySelectAction(FolderBean bean);
    }
}
