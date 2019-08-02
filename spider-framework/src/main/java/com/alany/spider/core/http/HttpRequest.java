package com.alany.spider.core.http;

import com.alany.spider.bean.HttpResult;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.proxy.ProxyFetchFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by alany on 2019/06/16.
 */
@Service("httpRequest")
public class HttpRequest {
    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private boolean useProxy = true; //默认请求时使用代理

    private String url;

    private Map<String, ? extends Object> headers;
    private Map<String, ? extends Object> params;
    private String content;
    private ContentType contentType;

    private ProxyFetchFactory proxyFetchFactory = SpringContext.getBean(ProxyFetchFactory.class);

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpRequest setHeaders(Map<String, Object> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequest setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public HttpRequest setContent(String content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
        return this;
    }

    public HttpRequest setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
        return this;
    }

    public CloseableHttpClient getSSLHttpClient() throws Exception {
        //设置代理IP、端口、协议
        RequestConfig config = null;
        if (useProxy && proxyFetchFactory != null){
            List<HttpHost> proxyHosts = proxyFetchFactory.getProxyHosts();
            if (proxyHosts != null && !proxyHosts.isEmpty()) {
                int index = new Random().nextInt(proxyHosts.size());
                HttpHost host = proxyHosts.get(index);
                logger.info("url: " + url + ", proxy: " + host.toHostString());
                //请求配置，设置链接超时和读取超时
                config = RequestConfig.custom().setProxy(host).setConnectTimeout(30000).setSocketTimeout(30000).build();
            }
        }

        if (config == null) {
            config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
        }

        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultRequestConfig(config).build();
        } catch (Exception e) {
            throw e;
        }
    }

    public HttpResult doGet() {
        try {
            //HttpClient client = HttpClients.createDefault();
            HttpClient client = getSSLHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, ? extends Object> header : headers.entrySet()) {
                    request.setHeader(header.getKey(), String.valueOf(header.getValue()));
                }
            }

            if (params != null && params.size() > 0) {
                StringBuilder sb = new StringBuilder();
                if (!url.contains("?")) {
                    sb.append("?");
                }
                for (Map.Entry<String, ? extends Object> param : params.entrySet()) {
                    sb.append(param.getKey() + "=" + param.getValue() + "&");
                }
                sb.deleteCharAt(sb.lastIndexOf("&"));
                url = url + sb.toString();
            }
            HttpResponse response = client.execute(request, new BasicHttpContext());
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResult doPost() {
        try {
            //HttpClient client = HttpClients.createDefault();
            HttpClient client = getSSLHttpClient();
            //发送get请求
            HttpPost request = new HttpPost(url);

            //设置url
            request.setURI(new URI(url));

            setParams(request);

            HttpResponse response = client.execute(request);
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResult doPut (){
        try {
            HttpClient client = getSSLHttpClient();
            // 实例化HTTP方法
            HttpPut request = new HttpPut();
            request.setURI(new URI(url));

            setParams(request);

            HttpResponse response = client.execute(request);
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        }
        catch(Exception e){
            e.printStackTrace();

            return null;
        }
    }

    public HttpResult doDelete() {
        try {
            HttpClient client = getSSLHttpClient();
            HttpDelete request = new HttpDelete(url);
            //设置url
            request.setURI(new URI(url));

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, ? extends Object> header : headers.entrySet()) {
                    request.setHeader(header.getKey(), String.valueOf(header.getValue()));
                }
            }

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            HttpResponse response = client.execute(request);
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setParams(HttpEntityEnclosingRequestBase request) throws UnsupportedEncodingException {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, ? extends Object> header : headers.entrySet()) {
                request.setHeader(header.getKey(), String.valueOf(header.getValue()));
            }
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        //设置参数
        if (params != null && params.size() > 0) {
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        }
        //设置内容
        if (content != null) {
            contentType = contentType == null ? ContentType.APPLICATION_JSON : contentType;
            request.setEntity(new StringEntity(content, contentType));
        }
    }
}
