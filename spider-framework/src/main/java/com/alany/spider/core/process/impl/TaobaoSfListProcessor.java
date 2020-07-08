package com.alany.spider.core.process.impl;

import com.alany.spider.bean.ExecuteContent;
import com.alany.spider.common.BizEnum;
import com.alany.spider.core.process.AbstractItemProcessor;
import com.alany.spider.core.process.AbstractListProcessor;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alany on 2020/7/1.
 */
@Service
public class TaobaoSfListProcessor extends AbstractListProcessor {

    public static final String API_NEWS_URL = "https://sf.taobao.com/item_list.htm?category=50025969&auction_source=0&city=%B9%E3%D6%DD&sorder=2&st_param=5&auction_start_seg=-1&page=";


    private int maxPageNumber = 200; //最大的分页数


    @Override
    public List<AbstractItemProcessor> getItemProcessors() {
        List<AbstractItemProcessor> list = new ArrayList<>();
        for (int i = 1; i < maxPageNumber; i++) {
            ExecuteContent executeContent = new ExecuteContent();
            executeContent.setUrl(API_NEWS_URL + i);
            executeContent.setBusiness(getBusiness());
            Map<String, Object> params = new HashMap();
            params.put("pageNumber", i);
            executeContent.setParams(JSON.toJSONString(params));
            TaobaoSfItemProcessor sfItemProcessor = new TaobaoSfItemProcessor();
            sfItemProcessor.setExecuteContent(executeContent);
            list.add(sfItemProcessor);
        }
        return list;
    }

    @Override
    public String getBusiness() {
        return BizEnum.tabobao.getName();
    }
}
