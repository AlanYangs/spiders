package com.alany.spider.core.proxy;

import com.alany.spider.bean.HttpProxy;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.http.UserAgentService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * Created by yangangui on 2018/12/5.
 */
@Component
public abstract class AbstractProxyFetcher {

    protected List<String> userAgentList = null;

    private Random random = new Random();

    public UserAgentService userAgentService = SpringContext.getBean(UserAgentService.class);

    private ProxyFetchFactory proxyFetchFactory = SpringContext.getBean(ProxyFetchFactory.class);

    @PostConstruct
    public void init() {
        userAgentList = userAgentService.getUserAgentList();
        proxyFetchFactory.addService(this);
    }

    public abstract String getBusiness();

    public abstract List<HttpProxy> fetchProxy();

    public String getRandomUserAgent(){
        return userAgentList.get(random.nextInt(userAgentList.size()));
    }

}
