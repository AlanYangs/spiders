package com.tdstack.core.parser;

import com.tdstack.core.http.HttpResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by yangangui on 2018/11/20.
 */
@Service
public abstract class CommonParser<T> {
    private static final Logger LOGGER = Logger.getLogger(CommonParser.class);

    public List<String> userAgentList = new ArrayList<>();

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void init() {
        List<String> list = new ArrayList<>();
        String defaultPath = this.getClass().getResource("/useragents.txt").getPath();
        File configFile = new File(defaultPath);
        try {
            list = FileUtils.readLines(configFile, "UTF-8");
            if (list != null && !list.isEmpty()) {
                //去重处理
                LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
                set.addAll(list);
                list.clear();
                list.addAll(set);
                userAgentList.addAll(list);
            }
        } catch (IOException e) {
            LOGGER.error("read user agent config file meet error:", e);
        }
        LOGGER.info("init userAgent config finished, userAgent list size=" + (list == null ? 0 : list.size()));
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
     * 入口：整合流程
     */
    public void process() {
        HttpResult result = request();
        List<T> list = parse(result);
        store(list);
    }

}
