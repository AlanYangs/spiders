package com.alany.spider.core.proxy.impl;

import com.alany.spider.bean.HttpProxy;
import com.alany.spider.bean.HttpResult;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.http.HttpRequest;
import com.alany.spider.core.proxy.AbstractProxyFetcher;
import com.alany.spider.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alany on 2019/7/26.
 */
@Service
public class GithubProxyFetcher extends AbstractProxyFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubProxyFetcher.class);
    private static final String PROXY_LIST_URL = "https://raw.githubusercontent.com/fate0/proxylist/master/proxy.list";

    private HttpRequest httpRequest = SpringContext.getBean(HttpRequest.class);

    @Override
    public String getBusiness() {
        return "github";
    }

    @Override
    public List<HttpProxy> fetchProxy() {
        List<HttpProxy> list = new ArrayList<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", getRandomUserAgent());
        HttpResult result = httpRequest.setUrl(PROXY_LIST_URL).setHeaders(headers).doGet();
        if (result == null || StringUtils.isBlank(result.getContent())) {
            return list;
        }
        String[] lines = result.getContent().split("\\n");
        if (lines != null && lines.length > 0) {
            for (String line : lines) {
                try {
                    JSONObject json = JSON.parseObject(line);
                    int port = json.getInteger("port");
                    String type = json.getString("type");
                    JSONArray addrArray = json.getJSONArray("export_address");
                    if (addrArray == null || addrArray.size() < 1) {
                        continue;
                    }
                    for (int i = 0; i < addrArray.size(); i++) {
                        String address = addrArray.getString(i);
                        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
                            continue;
                        }
                        HttpProxy httpProxy = new HttpProxy(address, port);
                        if (StringUtils.isNotBlank(type)) {
                            httpProxy.setType(type.toLowerCase());
                        }
                        httpProxy.setProvider(getBusiness());
                        list.add(httpProxy);
                    }
                } catch (Exception e) {
                    LOGGER.error("fetch proxy meet error: ", e);
                }
            }
        }
        LOGGER.info("fetch [" + getBusiness() + "] proxy list size=" + list.size());
        return list;
    }
}
