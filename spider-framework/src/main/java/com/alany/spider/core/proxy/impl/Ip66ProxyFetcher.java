package com.alany.spider.core.proxy.impl;


import com.alany.spider.bean.HttpProxy;
import com.alany.spider.core.proxy.AbstractProxyFetcher;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alany on 2019/7/26.
 */
@Service("ip66ProxyService")
public class Ip66ProxyFetcher extends AbstractProxyFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ip66ProxyFetcher.class);
    private static final String PAGE_URL_FORMAT = "http://www.66ip.cn/%d.html";

    @Override
    public String getBusiness() {
        return "66ip";
    }

    @Override
    public List<HttpProxy> fetchProxy() {
        Document doc = null;
        List<HttpProxy> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < 10; i++) {
            String url = String.format(PAGE_URL_FORMAT, i);
            try {
                doc = Jsoup.connect(url)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "zh-CN,zh;q=0.9")
                        .header("User-Agent", getRandomUserAgent())
                        .header("Host", "www.66ip.cn")
                        .timeout(30 * 1000)
                        .get();

                Elements trElms = doc.getElementById("main").select("tr");
                if (trElms != null) {
                    for (int j = 0, length = trElms.size(); j < length; j++) {
                        if (j == 0) {
                            continue;
                        }
                        String ip = trElms.get(j).select("td").get(0).text();
                        String port = trElms.get(j).select("td").get(1).text();
                        if (StringUtil.isBlank(ip) || StringUtil.isBlank(port)) {
                            continue;
                        }
                        HttpProxy httpProxy = new HttpProxy(ip, Integer.parseInt(port));
                        httpProxy.setProvider(getBusiness());
                        list.add(httpProxy);
                    }
                }
                Thread.sleep(random.nextInt(5) * 1000);
            } catch (Exception e) {
                LOGGER.error("fetch proxy meet error with url["+ url +"]: ", e);
            }
        }
        LOGGER.info("fetch [" + getBusiness() + "] proxy list size=" + list.size());
        return list;
    }
}
