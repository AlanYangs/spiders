package com.alany.spider.core.process;

import com.alany.spider.bean.ExecuteContent;
import com.alany.spider.bean.HttpResult;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.http.UserAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alany on 2019/4/20.
 */
public abstract class AbstractItemProcessor<T> implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemProcessor.class);

    public List<String> userAgentList = new ArrayList<>();

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ExecuteContent executeContent;

    public void setExecuteContent(ExecuteContent executeContent) {
        this.executeContent = executeContent;
    }

    /**
     * 请求
     *
     * @return
     */
    protected abstract HttpResult request();

    /**
     * 解析
     */
    protected abstract List<T> parse(HttpResult result);

    /**
     * 入库
     */
    protected abstract void store(List<T> list);

    /**
     * 是否有下一页
     */
    protected abstract boolean hasMore();


    /**
     * 入口：整合流程
     */
    public void process() throws InterruptedException {
        UserAgentService userAgentService = SpringContext.getBean(UserAgentService.class);
        userAgentList = userAgentService.getUserAgentList();
        HttpResult result = null;
        List<T> list = null;
        int retryTimes = 0;
        do {
            while (result == null && retryTimes < 3) { //请求失败重试3次
                result = request();
                retryTimes ++;
                Thread.sleep(5000);
            }

            list = parse(result);
            store(list);
        } while (hasMore());
    }

    @Override
    public void run() {
        LOGGER.info("executing processor: " + executeContent.toString());
        try {
            process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
