package com.tdstack.core.proxy;

import org.apache.http.HttpHost;

/**
 * Created by yangangui on 2018/12/5.
 */
public class HttpProxy {
    private String address;         //地址

    private int port;               //端口

    private String type = "http";            //类型

    private String provider;        //代理商

    private boolean isValid;

    public HttpProxy(){}

    public HttpProxy(String address, int port){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public HttpHost toHost(){
        return new HttpHost(address, port, "http");
    }
}
