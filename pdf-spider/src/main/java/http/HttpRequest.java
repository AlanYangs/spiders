package http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by alanyangs on 2017/12/16.
 */
public class HttpRequest {

    private String url;

    private Map<String, Object> headers;
    private Map<String, Object> params;
    private String content;
    private ContentType contentType;

    public HttpRequest(String url) {
        this.url = url;
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

    public static CloseableHttpClient getSSLHttpClient() throws Exception {
        //请求配置，设置链接超时和读取超时
        RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
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

    public HttpResponse doGet() {
        //System.setProperty("https.protocols", "SSLv3");//TLSv1.2,TLSv1.1,TLSv1.0,SSLv3,SSLv2Hello
        try {
            //HttpClient client = HttpClients.createDefault();
            HttpClient client = getSSLHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> header : headers.entrySet()) {
                    request.setHeader(header.getKey(), String.valueOf(header.getValue()));
                }
            }

            if (params != null && params.size() > 0) {
                StringBuilder sb = new StringBuilder();
                if (!url.contains("?")) {
                    sb.append("?");
                }
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    sb.append(param.getKey() + "=" + param.getValue() + "&");
                }
                sb.deleteCharAt(sb.lastIndexOf("&"));
                url = url + sb.toString();
            }
            HttpResponse response = client.execute(request, new BasicHttpContext());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse doPost() {
        try {
            //HttpClient client = HttpClients.createDefault();
            HttpClient client = getSSLHttpClient();
            //发送get请求
            HttpPost request = new HttpPost(url);

            //设置url
            request.setURI(new URI(url));

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> header : headers.entrySet()) {
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

            HttpResponse response = client.execute(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void doDownload(String path, String url) throws Exception {

        HttpClient httpClient = getSSLHttpClient();
        URL  Url = new URL(url);
        String paths = Url.getPath();
        String fileName = "";
        for (String param : paths.split("/")) {
            if (param.toLowerCase().endsWith(".pdf")) {
                fileName = URLDecoder.decode(param, "UTF-8");
                break;
            }
        }
        fileName = path + fileName;

        File file = new File(fileName);
        if(file.exists()) {
            file.delete();
        }
        try {
            //使用file来写入本地数据
            file.createNewFile();
            FileOutputStream outStream = new FileOutputStream(fileName);

            //执行请求，获得响应
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url), new BasicHttpContext());
            int code = httpResponse.getStatusLine().getStatusCode();
            System.out.println("[DOWNLOADING STATUS] get response status [" + httpResponse.getStatusLine() + "] for file :" + file.getName());
            if (code == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream inStream = httpEntity.getContent();
                while (true) {//这个循环读取网络数据，写入本地文件
                    byte[] bytes = new byte[1024 * 1024]; //1M
                    int k = inStream.read(bytes);
                    if (k >= 0) {
                        outStream.write(bytes, 0, k);
                        outStream.flush();
                    } else break;
                }
                inStream.close();
                outStream.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        String url = "https://page74.ctfile.com/get_file_url.php?uid=14147674&fid=232459688&folder_id=0&fid=232459688&file_chk=71756252bdc496e3ac857062fd6fb16b&mb=0&app=1&verifycode=&rd=12345";
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Host", "14147674.144.ctc.data.tv002.com:443");
        headers.put("Accept-Encoding","gzip, deflate");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
        HttpResponse response = new HttpRequest(url).setHeaders(null).doGet();

        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(response.getEntity()));

        System.out.println("[INFO] Download From : " + url);

        String path = "E:\\data\\pdf\\";

    }

}
