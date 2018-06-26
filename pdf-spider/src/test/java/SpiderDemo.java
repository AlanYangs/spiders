import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by alanyangs on 2018/6/25.
 */
public class SpiderDemo {

    private static final String MAIN_HOST = "https://page74.ctfile.com";
    private static final String UID = "14147674";
    private static final String DATA_PATH = "E:\\data\\pdf\\";

    /**
     * 多线程处理
     * @throws Exception
     */
    @Test
    public void crawlAllWithMultithread() throws Exception {
        List<String> urls = getDownloadUrls();
        int size = urls.size() > 1000 ? 100 : urls.size() / 10;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(size);
        for (final String url : urls) {
            final String name = getFileName(url);
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        System.out.println("[Begin] task is begin to run with download file: " + name);
                        HttpRequest.doDownload(DATA_PATH, url);
                        System.out.println("[End] task have finished to run with download file: " + name);
                    } catch (Exception e) {
                        System.err.println("[Error] task meet error when process url: " + url);
                        e.printStackTrace();
                    }
                }
            });
        }

         fixedThreadPool.shutdown();
        try {//等待直到所有任务完成
            fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单线程处理
     * @throws Exception
     */
    @Test
    public void crawlAllWithSingleThread() throws Exception {
        List<String> urls = getDownloadUrls();
        for (String url : urls) {
            String name = getFileName(url);
            try {
                System.out.println("[Begin] task is begin to run with download file: " + name);
                HttpRequest.doDownload(DATA_PATH, url);
                System.out.println("[End] task have finished to run with download file: " + name);
            } catch (Exception e) {
                System.err.println("[Error] task meet error when process url: " + url);
                e.printStackTrace();
            }
        }
    }

    private String getFileName(String url) throws Exception {
        URL  Url = new URL(url);
        String paths = Url.getPath();
        String fileName = "";
        for (String param : paths.split("/")) {
            if (param.toLowerCase().endsWith(".pdf")) {
                fileName = URLDecoder.decode(param, "UTF-8");
                break;
            }
        }
        return fileName;
    }

    private List<String> getDownloadUrls() throws Exception {
        List<String> urls = new ArrayList<String>();
        Map<String, String> urlMap = getDownloadPageUrls();
        int rndCode = new Random(1000).nextInt() + 100;
        String urlFormat = "https://page74.ctfile.com/get_file_url.php?uid=" + UID + "&fid=%s&folder_id=0&file_chk=%s&mb=0&app=0&verifycode=&rd=" + rndCode;
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            String fid = entry.getKey();
            String file_chk = getFileChk(entry.getValue());

            if (StringUtils.isNotEmpty(file_chk)) {
                String url = String.format(urlFormat, fid, file_chk);
                HttpResponse response = new HttpRequest(url).doGet();
                String responseBody = EntityUtils.toString(response.getEntity());

                JSONObject data = JSONObject.parseObject(responseBody);
                String downurl = data.getString("downurl");
                if (StringUtils.isNotEmpty(downurl)) {
                    urls.add(downurl);
                }
            }
        }
        return urls;
    }

    private Map<String, String> getDownloadPageUrls() throws Exception {
        Map<String, String> urlMap = new HashMap<String, String>();
        String urlFormat = "https://page74.ctfile.com/iajax_guest.php?item=file_act&action=file_list&task=file_list&folder_id=21645009&uid=" + UID + "&display_subfolders=1&t=1529909656&k=3de0d7f0ad5df1e6d6b71c4b43ce10d6&sEcho=%d&iColumns=4&sColumns=&iDisplayStart=%d&iDisplayLength=%d";
        int page = 1;
        int pageSize = 100;
        int pageStart = 1;
        int pageCount = pageSize;
        while (true) {
            String url = String.format(urlFormat, page, pageStart, pageSize);
            HttpResponse response = new HttpRequest(url).doGet();
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject data = JSONObject.parseObject(responseBody);
            pageCount = data.getInteger("iTotalRecords");

            JSONArray dataList = data.getJSONArray("aaData");
            Document doc = null;
            for (int i = 0; i < dataList.size(); i++) {
                JSONArray item = dataList.getJSONArray(i);
                doc = Jsoup.parse(item.get(0).toString());
                Element tdId = doc.select("#file_ids").first();
                String id = tdId.attr("value");

                doc = Jsoup.parse(item.get(1).toString());
                Element tdUrl = doc.select("a[href]").first();
                String url1 = tdUrl.attr("href");

                if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(url1)) {
                    url1 = MAIN_HOST + url1.replace("//", "/5a8841/");
                    urlMap.put(id, url1);
                }
            }
            if (pageSize + pageStart > pageCount) {
                break;
            }
            page++;
            pageStart += pageSize;
        }
        System.out.println("have fetched download page url size :" + urlMap.size());
        return urlMap;
    }

    private String getFileChk(String url) throws IOException {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Host", new URL(url).getHost());
        headers.put("Cookie", "PHPSESSID=eb2vqs2s7pgjkuuvqrq2aslov4; clicktopay=" + System.currentTimeMillis() + "; unique_id=5a8841; ua_checkmutilogin=OoDN6nCiRd;");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
        HttpResponse response = new HttpRequest(url).setHeaders(headers).doGet();
        String responseBody = EntityUtils.toString(response.getEntity());

        String chkStr = "";
        Document doc = Jsoup.parse(responseBody);
        Element link = doc.select("#free_down_link").first();
        if (link != null) {
            String text = link.attr("onclick");
            if (StringUtils.isNotEmpty(text)) {
                text = text.substring(text.indexOf("("), text.indexOf(")"));
                String[] arr = text.split(",");
                chkStr = arr[2];
                chkStr = chkStr.replaceAll("'", "").trim();
            }
        }
        return chkStr;
    }
}
