package com.kanzhun.manager.util;

/**
 * Created by zhouyou on 2016/6/24.
 */
public enum Type {
    /**
     * 先进先出
     */
    FIFO(0),
    /**
     * 后进先出
     */
    LIFO(1);

    private int type;

    Type(int type) {
        this.type = type;
    }

    public int get() {
        return type;
    }

}
