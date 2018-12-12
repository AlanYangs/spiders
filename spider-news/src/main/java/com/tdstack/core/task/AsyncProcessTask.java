package com.tdstack.core.task;

import com.tdstack.common.SpiderProcessor;
import com.tdstack.core.process.CommonProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
 * Created by yangangui on 2018/11/23.
 */
@Service("asyncProcessTask")
public class AsyncProcessTask implements ApplicationContextAware {
    private static final Logger LOGGER = Logger.getLogger(AsyncProcessTask.class);

    private ApplicationContext context;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        startAllProcessors();
    }

    public void scheduleAllProcessors(){
        LOGGER.info("schedule task start with " + sdf.format(new Date()));
        startAllProcessors();
        LOGGER.info("schedule task end with " + sdf.format(new Date()));
    }

    public void startAllProcessors(){
        List<CommonProcessor> processors = getAllProcessors();
        if (processors == null || processors.isEmpty()) {
            return;
        }

        ExecutorService es = Executors.newFixedThreadPool(5);
        for (CommonProcessor processor : processors) {
            es.submit(processor);
        }
        es.shutdown();
        try {
            es.awaitTermination(60, TimeUnit.MINUTES);//最大等待60min
        } catch (InterruptedException e) {
        }
    }

    public List<CommonProcessor> getAllProcessors(){
        List<CommonProcessor> processors = new ArrayList<CommonProcessor>();
        Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(SpiderProcessor.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                CommonProcessor processor = (CommonProcessor) serviceBean;
                processors.add(processor);
            }
        }
        LOGGER.info("fetch processor size=" + processors.size());
        return processors;
    }
}
