package com.tdstack.core.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tdstack.bean.BaseBean;
import com.tdstack.core.dao.CouldDBService;
import com.tdstack.core.http.HttpRequest;
import com.tdstack.core.http.HttpResult;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangangui on 2018/11/16.
 */

public abstract class AbstractCouldDBService<T extends BaseBean> implements CouldDBService <T>{
    private static Logger logger = Logger.getLogger(AbstractCouldDBService.class);

    private static final String APP_ID = "your appId";
    private static final String API_KEY = "your key";

    private static final String API_HOST_BASE = "https://api2.bmob.cn/1/";

    private static final String API_TABLE_URL = API_HOST_BASE + "classes/%s";

    private static Map<String,Object> headers;

    static {
        headers = new HashMap<>();
        headers.put("X-Bmob-Application-Id", APP_ID);
        headers.put("X-Bmob-REST-API-Key", API_KEY);
        headers.put("Content-Type", "application/json");
    }

    public abstract Class<T> setClass();

    public abstract String setTable();

    @Override
    public List<T> query(String whereAs) {
        String url = String.format(API_TABLE_URL, setTable());
        if (whereAs != null && !whereAs.isEmpty()) {
            try {
                whereAs = URLEncoder.encode(whereAs, "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("encode params["+ whereAs +"] meet error: ", e);
            }
            url += "?where=" + whereAs;
        }
        logger.info("query url: " + url);
        HttpResult response = new HttpRequest(url).setHeaders(headers).doGet();
        if (response == null) {
            throw new RuntimeException("request error: response is null");
        }

        if (200 == response.getCode()) {
            try {
                String res = response.getContent();
                JSONArray dataArray = JSON.parseObject(res).getJSONArray("results");
                if (dataArray != null && !dataArray.isEmpty()) {
                    List<T> list = new ArrayList<T>();
                    for (int i = 0, length = dataArray.size(); i < length; i++) {
                        JSONObject data = dataArray.getJSONObject(i);
                        list.add(data.toJavaObject(setClass()));
                    }
                    logger.info("query success and result size=" + list.size());
                    return list;
                }
            } catch (Exception e) {
                logger.error("parse response meet error: ", e);
            }
        } else {
            logger.error("request error: response=" + response.toString());
        }
        return null;
    }

    @Override
    public boolean insert(T bean) {
        String url = String.format(API_TABLE_URL, setTable());

        if (bean == null) {
            logger.error("insert failed: bean is null");
            return false;
        }

        Map<String, Object> beanMap = bean.toMap();

        HttpResult response = new HttpRequest(url).setHeaders(headers)
                .setContent(JSON.toJSONString(beanMap), ContentType.APPLICATION_JSON).doPost();

        if (response == null) {
            throw new RuntimeException("request error: response is null");
        }

        if (201 == response.getCode()) {
            logger.info("success to insert bean ["+ JSON.toJSONString(beanMap) +"]");
            return true;
        } else {
            logger.error("failed to insert bean ["+ JSON.toJSONString(beanMap) +"], msg:" + response.getContent());
            return false;
        }
    }

    @Override
    public void insertBatch(List<T> list) {
        String url = API_HOST_BASE + "batch";
        if (list == null || list.isEmpty()) {
            return;
        }

        JSONArray reqArray = new JSONArray();
        for (T t : list) {
            JSONObject item = new JSONObject();
            item.put("method", "POST");
            item.put("path", "1/classes/" + setTable());
            item.put("body", JSON.toJSONString(t.toMap()));
            reqArray.add(item);
        }
        JSONObject contentJson = new JSONObject();
        contentJson.put("requests", reqArray);
        HttpResult response = new HttpRequest(url).setHeaders(headers)
                .setContent(contentJson.toJSONString(), ContentType.APPLICATION_JSON).doPost();

        if (response == null) {
            throw new RuntimeException("request error: response is null");
        }
        System.out.println(response.toString());
    }

    @Override
    public void update() {

    }

    @Override
    public void replace() {

    }

    @Override
    public void replaceBatch() {

    }

    @Override
    public boolean delete(String objectId) {
        if (objectId == null || "".equals(objectId)) {
            return false;
        }
        String url = String.format(API_TABLE_URL, setTable()) + "/" + objectId;

        HttpResult response = new HttpRequest(url).setHeaders(headers).doDelete();
        if (response == null) {
            throw new RuntimeException("request error: response is null");
        }

        JSONObject retJson = JSON.parseObject(response.getContent());
        boolean isSuccess = "ok".equalsIgnoreCase(retJson.getString("msg"));
        if (isSuccess) {
            logger.info("success to delete row [objectId="+ objectId +"]");
        } else {
            logger.error("failed to delete row [objectId="+ objectId +"], msg:" + response.getContent());
        }
        return isSuccess;
    }
}
