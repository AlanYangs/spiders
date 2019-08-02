package com.alany.spider.core.proxy.impl;

import com.alany.spider.bean.HttpProxy;
import com.alany.spider.core.proxy.AbstractProxyFetcher;
import com.alany.spider.utils.StringUtils;
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
@Service("kuaidailiNewProxyService")
public class KuaidailiProxyFetcher extends AbstractProxyFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(KuaidailiProxyFetcher.class);

    private static final String KUAIDAILI_FREE_PROXY_URL = "https://www.kuaidaili.com/proxylist/%d";

    @Override
    public String getBusiness() {
        return "kuaidaili";
    }

    @Override
    public List<HttpProxy> fetchProxy() {
        Document doc = null;
        List<HttpProxy> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < 10; i++) {
            String url = String.format(KUAIDAILI_FREE_PROXY_URL, i);
            String refererUrl = i > 1 ?  String.format(KUAIDAILI_FREE_PROXY_URL, i-1) : "";
            try {
                doc = Jsoup.connect(url)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "zh-CN,zh;q=0.9")
                        .header("User-Agent", getRandomUserAgent())
                        .header("Host", "www.kuaidaili.com")
                        .header("Referer", refererUrl)
                        .timeout(30 * 1000)
                        .get();

                Elements ipElms = doc.select("td[data-title=\"IP\"]");
                Elements portElms = doc.select("td[data-title=\"PORT\"]");
                Elements typeElms = doc.select("td[data-title=\"类型\"]");
                if (ipElms != null) {
                    for (int j = 0, length = ipElms.size(); j < length; j++) {
                        String ip = ipElms.get(j).text();
                        String port = portElms.get(j).text();
                        if (StringUtil.isBlank(ip) || StringUtil.isBlank(port)) {
                            continue;
                        }
                        HttpProxy httpProxy = new HttpProxy(ip, Integer.parseInt(port));
                        String type = typeElms.get(j).text();
                        if (StringUtils.isNotBlank(type) && !type.contains(",")) {
                            httpProxy.setType(type.toLowerCase());
                        }
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
