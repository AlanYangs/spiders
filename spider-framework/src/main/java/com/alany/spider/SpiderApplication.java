package com.alany.spider;

import com.alany.spider.common.SpringContext;
import com.alany.spider.core.task.AsyncProcessTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.alany.spider"}) //扫描该包路径下的所有spring组件
@SpringBootApplication
public class SpiderApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpiderApplication.class, args);
		AsyncProcessTask asyncProcessTask = SpringContext.getBean(AsyncProcessTask.class);
		asyncProcessTask.initProxy();
		Thread.sleep(1000 * 30);
		asyncProcessTask.startAllProcessors();
	}

}
