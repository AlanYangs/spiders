package com.alany.spider.core.dao.mapper;

import com.alany.spider.bean.HouseBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HouseMapper {
	int batchInsert(List<HouseBean> tasks);
}
