package com.gy.utils;

import com.gy.dao.Hotword;
import com.gy.entity.HotSearchInfo;
import com.gy.entity.ItemInfo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * created by yangyu on 2019-09-27
 */
@Service
public class DBHelper {

    private static final Logger logger = LoggerFactory.getLogger(DBHelper.class.getName());

    public static String tableName;
    public static String dropTableName;
    public static boolean isDrop = false;

    @Autowired
    private Hotword hotword;

    public void dbChange() throws Exception{
        if (isDrop) {
            createTable(tableName);

            if (StringUtils.isNotEmpty(dropTableName)) {
                hotword.dropTable(dropTableName);
                logger.info("Drop table {} finished !",dropTableName);
            }
        } else {
            createTable(tableName);
        }
    }

    private void createTable(String name) throws Exception{
        if (StringUtils.isEmpty(name)){
            return;
        }

        int existResult = hotword.isTableExist(name);

        if (existResult > 0){
            return;
        }
        hotword.createTable(name);
        logger.info("Create table {}!",name);

        Map<String,Object> map = new HashMap<>();
        map.put("tableName",name);
        map.put("initId",1000000000);
        hotword.initId(map);
    }

    public <T>void insertMysql(T t) {
        try {
            if (t instanceof ItemInfo) {
//                ItemInfo itemInfo = (ItemInfo)t;
//                sql = String.format("INSERT INTO hot1688 (create_date,third_link,first_name,second_name,third_name,item_link,item_name,price,item_company,trading_index,evaluation) VALUES (NOW(),'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
//                        itemInfo.getThird_link(), itemInfo.getFirst_name(), itemInfo.getSecond_name(),
//                        itemInfo.getThird_name(), itemInfo.getItem_link(), itemInfo.getItem_name(), itemInfo.getPrice(), itemInfo.getItem_company(),
//                        itemInfo.getTrading_index(), itemInfo.getEvaluation());
            } else if (t instanceof HotSearchInfo){
                HotSearchInfo info = (HotSearchInfo)t;
                hotword.insertHotWord(info,tableName);
            }
        } catch (Exception ex) {
            logger.error("Insert into erp_ic hot1688 failed :", ex);
        }
    }

}
