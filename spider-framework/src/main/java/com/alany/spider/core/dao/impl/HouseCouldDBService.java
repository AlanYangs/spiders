package com.alany.spider.core.dao.impl;

import com.alany.spider.bean.HouseBean;
import org.springframework.stereotype.Service;

/**
 * Created by alany on 2019/7/29.
 */
@Service("houseCouldDBService")
public class HouseCouldDBService extends AbstractCouldDBService {

    @Override
    public Class setClass() {
        return HouseBean.class;
    }

    @Override
    public String setTable() {
        return "t_houses";
    }
}
