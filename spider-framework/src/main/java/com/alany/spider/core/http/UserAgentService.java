package com.alany.spider.core.http;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by yangangui on 2018/12/5.
 */
@Service
public class UserAgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAgentService.class);

    public List<String> userAgentList = new ArrayList<>();

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

    public List<String> getUserAgentList() {
        return userAgentList;
    }
}
