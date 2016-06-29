package com.kanzhun.manager.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyou on 2016/6/23.
 */
public class FolderBean {

    public long id;

    public String firstImagePath;

    public String folderName;

    public List<String> files = new ArrayList<>();

    public void setDir(String dir) {
        int lastIndexOf = dir.lastIndexOf("/");
        this.folderName = dir.substring(lastIndexOf);
    }

    public void setId(long id) {
        this.id = id;
        if (id < 0) {
            this.folderName = "所有图片";
        }
    }
}
