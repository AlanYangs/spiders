package com.alany.spider.core.dao.impl;

import com.alany.spider.bean.Article;
import org.springframework.stereotype.Service;

/**
 * Created by alany on 2019/7/29.
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
}
