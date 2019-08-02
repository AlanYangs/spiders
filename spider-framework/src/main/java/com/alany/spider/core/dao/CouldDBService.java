package com.alany.spider.core.dao;

import com.alany.spider.bean.BaseBean;

import java.io.IOException;
import java.util.List;

/**
 * Created by alany on 2019/7/29.
 */
public interface CouldDBService <T extends BaseBean>{

    /**
     *
     * @param whereAs json格式："{"column":"value"}"
     */
    List<T> query(String whereAs) throws IOException;

    boolean insert(T bean);

    void insertBatch(List<T> list);

    void update();

    void replace();

    void replaceBatch();

    boolean delete(String objectId);
}
