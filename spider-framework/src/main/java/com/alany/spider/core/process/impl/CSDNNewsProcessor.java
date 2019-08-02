package com.alany.spider.core.process.impl;

import com.alany.spider.bean.Article;
import com.alany.spider.bean.HttpResult;
import com.alany.spider.common.SpiderProcessor;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.dao.impl.ArticleCouldDBService;
import com.alany.spider.core.http.HttpRequest;
import com.alany.spider.core.process.AbstractItemProcessor;
import com.alany.spider.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yangangui on 2018/11/21.
 */
@SpiderProcessor
@Service("csdnNewsProcessor")
public class CSDNNewsProcessor extends AbstractItemProcessor<Article> {

    private static Logger logger = LoggerFactory.getLogger(CSDNNewsProcessor.class);

    private ArticleCouldDBService articleCouldDBService = SpringContext.getBean(ArticleCouldDBService.class);

    private HttpRequest httpRequest = SpringContext.getBean(HttpRequest.class);

    @Override
    public HttpResult request() {
        Map<String, Object> headers = new HashMap<>();
        if (userAgentList != null && !userAgentList.isEmpty()) {
            int index = new Random().nextInt(userAgentList.size());
            headers.put("User-Agent", userAgentList.get(index));
        }

        HttpResult result = httpRequest.setUrl(executeContent.getUrl()).setHeaders(headers).doGet();
        return result;
    }

    @Override
    public List<Article> parse(HttpResult result) {
        List<Article> list = new ArrayList<>();
        if (result != null && StringUtils.isNotEmpty(result.getContent())) {
            try {
                JSONObject root = JSON.parseObject(result.getContent());
                JSONArray articles = root.getJSONArray("articles");
                if (articles != null && !articles.isEmpty()) {
                    for (int i = 0, length = articles.size(); i < length; i++) {
                        JSONObject item = articles.getJSONObject(i);
                        Article article = new Article();
                        article.setSourceName(executeContent.getBusiness());
                        article.setTitle(item.getString("title"));
                        article.setArticleId(item.getString("id"));
                        article.setAuthor(item.getString("nickname"));
                        article.setUrl(item.getString("url"));
                        String category = item.getString("category");
                        if (StringUtils.isEmpty(category)) {
                            category = item.getString("tag");
                        }
                        article.setCategory(category);
                        article.setDescription(item.getString("desc"));
                        article.setImgUrl(item.getString("avatar"));
                        Date now = new Date();
                        article.setPublishTime(sdf.format(now));

                        if (StringUtils.isNotBlank(article.getTitle()) && StringUtils.isNotBlank(article.getUrl())) {
                            list.add(article);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("parse json to bean meet error:", e);
            }
        }
        return list;
    }

    @Override
    public void store(List<Article> list) {
        if (list != null && !list.isEmpty()) {
            for (Article article : list) {
                articleCouldDBService.insert(article);
            }
        }
    }

    @Override
    protected boolean hasMore() {
//        long currMs = System.currentTimeMillis();
//        Map<String, Object> paramsMap = (Map) JSON.parse(executeContent.getParams());
//        long offset = Long.parseLong(String.valueOf(paramsMap.get("offset")).substring(0,Long.toString(currMs).length()));//就取毫秒级别的时间戳作为比较
//        return offset < currMs;
        return false;
    }
}
