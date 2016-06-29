package com.kanzhun.manager.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by zhouyou on 2016/6/24.
 */
public class ImageSuffixFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        filename = filename.toLowerCase();
        return (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"));
    }
}
