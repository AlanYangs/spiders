package com.alany.spider;

import com.alany.spider.bean.Article;
import com.alany.spider.core.dao.impl.ArticleCouldDBService;
import com.alany.spider.core.proxy.ProxyFetchFactory;
import com.alany.spider.core.task.AsyncProcessTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpiderApplicationTests {

	@Autowired
	private AsyncProcessTask asyncProcessTask;

	@Autowired
	private ProxyFetchFactory proxyFetchFactory;

	@Autowired
	private ArticleCouldDBService articleCouldDBService;

	@Test
	public void testProxyFetch() {
		proxyFetchFactory.buildProxy();
	}

	@Test
	public void testArticleCouldDBSeervice(){
		List<Article> articles = new ArrayList<>();
		Article article = new Article();
		article.setArticleId("test12345678");
		article.setTitle("insert test");
		article.setSourceName("测试数据");
		articles.add(article);
		articleCouldDBService.insert(article);

		articles.clear();
		articles = articleCouldDBService.query("{\"articleId\":\"test12345678\"}");
		System.out.println(articles);
	}
}
