package com.tdstack.core.process;

import com.tdstack.core.http.HttpResult;
import com.tdstack.core.http.UserAgentService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangangui on 2018/11/20.
 */
@Service
public abstract class CommonProcessor<T> implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(CommonProcessor.class);

    public List<String> userAgentList = new ArrayList<>();

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UserAgentService userAgentService;

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
     * 入口：整合流程
     */
    public void process() {
        userAgentList = userAgentService.getUserAgentList();
        HttpResult result = request();
        List<T> list = parse(result);
        store(list);
    }

    @Override
    public void run() {
        process();
    }
}
