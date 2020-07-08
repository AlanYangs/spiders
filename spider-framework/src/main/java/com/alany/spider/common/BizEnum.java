package com.alany.spider.common;

/**
 * Created by alany on 2020/7/1.
 */
public enum BizEnum {
    tabobao("taobao");

    private String name;
    BizEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
