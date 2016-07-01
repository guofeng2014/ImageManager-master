package com.kanzhun.manager.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyou on 2016/6/23.
 */
public class FolderBean {

    /**
     * 列表封面图片地址
     */
    public String coverImagePath;
    /**
     * 文件夹名称
     */
    public String folderName;
    /**
     * 列表集合
     */
    public List<String> files = new ArrayList<>();

}
