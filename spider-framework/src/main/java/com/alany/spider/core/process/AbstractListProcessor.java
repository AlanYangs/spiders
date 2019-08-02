package com.alany.spider.core.process;

import java.util.List;

/**
 * Created by alany on 2019/4/15.
 */
public abstract class AbstractListProcessor {

    /**
     * 内容处理器列表
     * @return
     */
    public abstract List<AbstractItemProcessor> getItemProcessors();

    /**
     * 业务名称
     * @return
     */
    public abstract String getBusiness();
}
