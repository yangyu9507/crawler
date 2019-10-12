package com.gy.dao;


import com.gy.entity.HotSearchInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * created by yangyu at 2019-10-08
 */
@Mapper
public interface Hotword {

    void dropTable(@Param(value = "tableName") String tableName);

    void createTable(@Param(value = "tableName") String tableName);

    void initId(Map<String,Object> map);

    void insertHotWord(@Param(value = "info")HotSearchInfo info,@Param(value = "tableName")String tableName);

    int isTableExist(@Param(value = "tableName") String tableName);
}
