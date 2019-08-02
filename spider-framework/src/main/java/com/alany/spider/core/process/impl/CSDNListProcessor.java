package com.alany.spider.core.process.impl;

import com.alany.spider.bean.ExecuteContent;
import com.alany.spider.core.process.AbstractItemProcessor;
import com.alany.spider.core.process.AbstractListProcessor;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alany on 2019/7/25.
 */
@Service
public class CSDNListProcessor extends AbstractListProcessor {

    private static final String API_NEWS_URL = "https://www.csdn.net/api/articles?type=more&category=news&shown_offset=%s";

    private long offset = System.currentTimeMillis() * 1000; //初始offset值，16位


    private int maxPageNumber = 10; //最大的分页数


    @Override
    public List<AbstractItemProcessor> getItemProcessors() {
        List<AbstractItemProcessor> list = new ArrayList<>();
        for (int i = 0; i < maxPageNumber; i++) {
            ExecuteContent executeContent = new ExecuteContent();
            long currOffset = offset + i * 10; //由于接口每次返回10条记录，所以offset按10递增
            executeContent.setUrl(String.format(API_NEWS_URL, ""));//多次调试发现这种方式可以不传offset，接口每次会自动更新
            executeContent.setBusiness(getBusiness());
            Map<String, Object> params = new HashMap();
            params.put("offset", currOffset);
            executeContent.setParams(JSON.toJSONString(params));
            CSDNNewsProcessor csdnNewsProcessor = new CSDNNewsProcessor();
            csdnNewsProcessor.setExecuteContent(executeContent);
            list.add(csdnNewsProcessor);
        }
        return list;
    }

    @Override
    public String getBusiness() {
        return "CSDN";
    }
}
