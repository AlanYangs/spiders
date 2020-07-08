package com.alany.spider.core.process.impl;

import com.alany.spider.bean.HouseBean;
import com.alany.spider.bean.HttpResult;
import com.alany.spider.common.AddressType;
import com.alany.spider.common.BizEnum;
import com.alany.spider.common.SpiderProcessor;
import com.alany.spider.common.SpringContext;
import com.alany.spider.core.dao.mapper.HouseMapper;
import com.alany.spider.core.http.HttpRequest;
import com.alany.spider.core.process.AbstractItemProcessor;
import com.alany.spider.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by alany on 2020/7/1.
 */
@SpiderProcessor
@Service("taobaoSfItemProcessor")
public class TaobaoSfItemProcessor extends AbstractItemProcessor<HouseBean> {

    private static Logger logger = LoggerFactory.getLogger(TaobaoSfItemProcessor.class);

    private HttpRequest httpRequest = SpringContext.getBean(HttpRequest.class);

    private HouseMapper houseMapper = SpringContext.getBean(HouseMapper.class);

    private static int totalPage = 0;

    @Override
    public HttpResult request() {
        Map<String, Object> headers = new HashMap<>();
        if (userAgentList != null && !userAgentList.isEmpty()) {
            int index = new Random().nextInt(userAgentList.size());
            headers.put("User-Agent", userAgentList.get(index));
        }

        HttpResult result = httpRequest.setUrl(executeContent.getUrl()).setHeaders(headers).setUseProxy(false).doGet();
        return result;
    }

    @Override
    public List<HouseBean> parse(HttpResult result) {
        List<HouseBean> list = new ArrayList<>();
        if (result != null && StringUtils.isNotEmpty(result.getContent())) {
            try {
                Document document = Jsoup.parse(result.getContent());
                Element element = document.select("script[id=sf-item-list-data]").first();
                if (element == null) {
                    return null;
                }
                if (totalPage == 0) {
                    String totalPageStr = document.select(".page-total").first().ownText();
                    if (StringUtils.isNotBlank(totalPageStr)) {
                        totalPage = Integer.parseInt(totalPageStr);
                    }
                }
                String jsonText = element.html();
                JSONObject root = JSON.parseObject(jsonText);
                JSONArray dataArray = root.getJSONArray("data");
                if (dataArray != null && !dataArray.isEmpty()) {
                    for (int i = 0, length = dataArray.size(); i < length; i++) {
                        JSONObject item = dataArray.getJSONObject(i);

                        HouseBean bean = new HouseBean();
                        bean.setSourceName(BizEnum.tabobao.getName());
                        bean.setItemId(item.getString("id"));
                        String title = item.getString("title");
                        bean.setAddress(title);
                        bean.setCity(AddressType.regexAddress(title, AddressType.city));
                        bean.setLocation(AddressType.regexAddress(title, AddressType.county));
//                        if (title.contains("市") && title.contains("区")) {
//                            bean.setCity(title.substring(0, title.indexOf("市") + 1));
//                            bean.setLocation(title.substring(title.indexOf("市") + 1, title.indexOf("区") + 1));
//                        } else {
//                            bean.setCity("广州市");
//                        }

                        bean.setSellTotalPrice(item.getFloatValue("currentPrice"));
                        bean.setMarketTotalPrice(item.getFloatValue("marketPrice"));
                        if (bean.getMarketTotalPrice() < 1) {
                            bean.setMarketTotalPrice(item.getFloatValue("consultPrice"));
                        }
                        bean.setSellStatus(item.getString("status"));
                        Date date = new Date(item.getTimestamp("end").getTime());
                        bean.setSellDate(date);
                        bean.setItemUrl("http:" + item.getString("itemUrl"));

                        if (StringUtils.isNotBlank(bean.getItemId())) {
                            list.add(bean);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("parse json to bean meet error:", e);
            }
        }
        return list;
    }

    @Override
    public void store(List<HouseBean> list) {
        if (list != null && !list.isEmpty()) {
            houseMapper.batchInsert(list);
        }
    }

    @Override
    protected boolean hasMore() {

        return false;
    }
}
