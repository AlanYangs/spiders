package com.tdstack.core.proxy;

import com.tdstack.core.http.UserAgentService;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yangangui on 2018/12/5.
 */
@Service("kuaidailiFreeProxyService")
public class KuaidailiFreeProxyService extends AbstractHttpProxyService {
    private static final Logger LOGGER = Logger.getLogger(KuaidailiFreeProxyService.class);

    private static final String KUAIDAILI_FREE_PROXY_URL = "https://www.kuaidaili.com/free/inha/%d/";

    @Autowired
    private UserAgentService userAgentService;

    @Override
    public List<HttpProxy> fetchProxy() {
        Document doc = null;
        List<HttpProxy> list = new ArrayList<>();
        List<String> userAgentList = userAgentService.getUserAgentList();
        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            try {
                doc = Jsoup.connect(String.format(KUAIDAILI_FREE_PROXY_URL, i))
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, sdch")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                        .header("Cache-Control", "max-age=0")
                        .header("User-Agent", userAgentList.get(random.nextInt(userAgentList.size())))
                        .header("Cookie", "Hm_lvt_7ed65b1cc4b810e9fd37959c9bb51b31=1462812244; _gat=1; _ga=GA1.2.1061361785.1462812244")
                        .header("Host", "www.kuaidaili.com")
                        .header("Referer", "http://www.kuaidaili.com/free/outha/")
                        .timeout(30 * 1000)
                        .get();

                Elements ipElms = doc.select("[data-title=\"IP\"]");
                Elements portElms = doc.select("[data-title=\"PORT\"]");
                if (ipElms != null) {
                    for (int j = 0, length = ipElms.size(); j < length; j++) {
                        String ip = ipElms.get(j).text();
                        String port = portElms.get(j).text();
                        if (StringUtil.isBlank(ip) || StringUtil.isBlank(port)) {
                            continue;
                        }
                        HttpProxy httpProxy = new HttpProxy(ip, Integer.parseInt(port));
                        list.add(httpProxy);
                    }
                }
                Thread.sleep(random.nextInt(5) * 1000);
            } catch (Exception e) {
                LOGGER.error("fetch proxy meet error: ", e);
            }
        }
        LOGGER.info("fetch proxy list size=" + list.size());
        return list;
    }

}
