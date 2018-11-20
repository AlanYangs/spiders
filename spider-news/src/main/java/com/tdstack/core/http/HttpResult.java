package com.tdstack.core.http;

/**
 * Created by yangangui on 2018/11/20.
 */
public class HttpResult {
    private int code;

    private String content;

    private String msg;

    public HttpResult(){}

    public HttpResult(int code, String content){
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", content='" + content + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
