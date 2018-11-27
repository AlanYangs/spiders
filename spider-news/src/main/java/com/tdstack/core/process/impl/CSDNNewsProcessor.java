package com.tdstack.core.process.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tdstack.bean.Article;
import com.tdstack.common.SpiderProcessor;
import com.tdstack.core.dao.impl.ArticleCouldDBService;
import com.tdstack.core.http.HttpRequest;
import com.tdstack.core.http.HttpResult;
import com.tdstack.core.process.CommonProcessor;
import com.tdstack.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yangangui on 2018/11/21.
 */
@SpiderProcessor
@Service("csdnNewsParser")
public class CSDNNewsProcessor extends CommonProcessor<Article> {

    private static Logger logger = Logger.getLogger(CSDNNewsProcessor.class);

    private static final String API_NEWS_URL = "https://www.csdn.net/api/articles?type=more&category=news";

    @Autowired
    private ArticleCouldDBService articleCouldDBService;

    @Override
    public HttpResult request() {
        Map<String, Object> headers = new HashMap<>();
        if (userAgentList != null && !userAgentList.isEmpty()) {
            int index = new Random().nextInt(userAgentList.size());
            headers.put("User-Agent", userAgentList.get(index));
        }

        HttpResult result = new HttpRequest(API_NEWS_URL).setHeaders(headers).doGet();
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
                        article.setSourceName("CSDN");
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
}
