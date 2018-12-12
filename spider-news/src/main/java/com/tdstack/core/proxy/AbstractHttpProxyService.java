package com.tdstack.core.proxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tdstack.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangangui on 2018/12/5.
 */
public abstract class AbstractHttpProxyService {

    public static final int CACHE_MAX_SIZE = 1000;

    public LoadingCache<String, List<HttpHost>> cache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_MAX_SIZE)
            .refreshAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<HttpHost>>() {
                @Override
                public List<HttpHost> load(String key) throws Exception {
                    return buildProxy();
                }
            });

    public abstract List<HttpProxy> fetchProxy();

    public List<HttpHost> getProxy(){
        try {
            if (cache.size() > 0) {
                return cache.get("ALL");
            }
        } catch (ExecutionException e) {
        }
        return null;
    }

    public List<HttpHost> buildProxy(){
        List<HttpProxy> proxies = fetchProxy();
        List<HttpHost> hosts = new ArrayList<>();
        if (proxies != null && !proxies.isEmpty()) {
            ExecutorService es = Executors.newFixedThreadPool(10);
            for (final HttpProxy proxy : proxies) {
                es.submit(new Runnable() {
                    @Override
                    public void run() {
                        proxy.setValid(checkHost(proxy));
                    }
                });
            }
            es.shutdown();
            try {
                es.awaitTermination(20, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
            }

            for (HttpProxy proxy : proxies) {
                if (proxy.isValid()) {
                    hosts.add(proxy.toHost());
                }
            }
        }
        return hosts;
    }

    private boolean checkHost(HttpProxy httpProxy){
        boolean isValid = false;
        InetSocketAddress addr = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            //Proxy类代理方法
            URL url = new URL("http://www.baidu.com");
            // 创建代理服务器
            addr = new InetSocketAddress(httpProxy.getAddress(), httpProxy.getPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
            conn = url.openConnection(proxy);
            in = conn.getInputStream();
            String content = IOUtils.toString(in);
            isValid = StringUtils.isNotBlank(content) && content.indexOf("百度") > 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(httpProxy.getAddress() + " : " + isValid);
        return isValid;
    }

}
