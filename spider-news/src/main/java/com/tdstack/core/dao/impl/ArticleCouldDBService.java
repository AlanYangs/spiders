package com.tdstack.core.dao.impl;

import com.tdstack.bean.Article;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangangui on 2018/11/19.
 */
@Service("articleCouldDBService")
public class ArticleCouldDBService extends AbstractCouldDBService {

    @Override
    public Class setClass() {
        return Article.class;
    }

    @Override
    public String setTable() {
        return "t_articles";
    }

    public static void main(String[] args) throws Exception {
        ArticleCouldDBService demo = new ArticleCouldDBService();
        List<Article> articles = demo.query(null);
        System.out.println(articles);

        Article article = new Article();
        article.setArticleId("test12345678");
        article.setTitle("insert test");
        article.setSourceName("测试数据");
        articles.clear();
        articles.add(article);
        demo.insert(article);

        articles = demo.query("{\"articleId\":\"test12345678\"}");
        System.out.println(demo.delete(articles.get(0).getObjectId()));
    }
}
