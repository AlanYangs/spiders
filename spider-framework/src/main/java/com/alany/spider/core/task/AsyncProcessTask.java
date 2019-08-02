package com.alany.spider.core.task;

import com.alany.spider.common.SpiderProcessor;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.process.AbstractItemProcessor;
import com.alany.spider.core.process.AbstractListProcessor;
import com.alany.spider.core.proxy.ProxyFetchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by alany on 2019/07/23.
 */
@Service("asyncProcessTask")
public class AsyncProcessTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncProcessTask.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ApplicationContext context = SpringContext.getApplicationContext();

    /**
     * 初始化代理爬取，触发代理爬取
     */
    public void initProxy() {
        Map<String, ProxyFetchFactory> serviceBeanMap = context.getBeansOfType(ProxyFetchFactory.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                ProxyFetchFactory service = (ProxyFetchFactory) serviceBean;
                service.buildProxy();
            }
        }
    }

    /**
     * 按定时调度触发所有业务爬取任务
     */
    @Scheduled(cron = "0 0 0 1 * ?") //每天凌晨0点执行一次
    public void scheduleAllProcessors() {
        LOGGER.info("schedule task start with " + sdf.format(new Date()));
        startAllProcessors();
        LOGGER.info("schedule task end with " + sdf.format(new Date()));
    }

    /**
     * 触发所有业务爬取任务
     */
    public void startAllProcessors() {
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<AbstractListProcessor> processors = getAllListProcessors();
        if (processors == null || processors.isEmpty()) {
            return;
        }
        for (AbstractListProcessor listProcessor : processors) {
            for (AbstractItemProcessor itemProcessor : listProcessor.getItemProcessors()) {
                es.execute(itemProcessor);
            }
        }
        try {
            es.awaitTermination(60, TimeUnit.SECONDS);//最大等待60s
        } catch (InterruptedException e) {
        } finally {
            es.shutdown();
        }
    }

    /**
     * 按业务名称触发爬取任务
     *
     * @param business
     */
    public void startProcessorsByBusiness(String business) {
        List<AbstractListProcessor> processors = getAllListProcessors();
        if (processors == null || processors.isEmpty()) {
            return;
        }
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (AbstractListProcessor listProcessor : processors) {
            if (business.equalsIgnoreCase(listProcessor.getBusiness())) {
                for (AbstractItemProcessor itemProcessor : listProcessor.getItemProcessors()) {
                    es.execute(itemProcessor);
                }
                break;
            }
        }
        try {
            es.awaitTermination(60, TimeUnit.SECONDS);//最大等待60s
        } catch (InterruptedException e) {
        } finally {
            es.shutdown();
        }
    }

    private List<AbstractListProcessor> getAllListProcessors() {
        List<AbstractListProcessor> processors = new ArrayList<>();
        Map<String, AbstractListProcessor> serviceBeanMap = context.getBeansOfType(AbstractListProcessor.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                AbstractListProcessor processor = (AbstractListProcessor) serviceBean;
                processors.add(processor);
            }
        }
        LOGGER.info("fetch list processor size=" + processors.size());
        return processors;
    }

    private List<AbstractItemProcessor> getAllItemProcessors() {
        List<AbstractItemProcessor> processors = new ArrayList<>();
        Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(SpiderProcessor.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                AbstractItemProcessor processor = (AbstractItemProcessor) serviceBean;
                processors.add(processor);
            }
        }
        LOGGER.info("fetch processor size=" + processors.size());
        return processors;
    }
}
