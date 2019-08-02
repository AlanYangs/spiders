package com.alany.spider.bean;

/**
 * Created by alany on 2019/4/15.
 */
public class ExecuteContent {

    private long start;

    private long end;

    private String business;

    private String url;

    private String params;

    private HttpResult httpResult;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public HttpResult getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(HttpResult httpResult) {
        this.httpResult = httpResult;
    }

    @Override
    public String toString() {
        return "ExecuteContent{" +
                "start=" + start +
                ", end=" + end +
                ", business='" + business + '\'' +
                ", url='" + url + '\'' +
                ", params='" + params + '\'' +
                ", httpResult=" + httpResult +
                '}';
    }
}
