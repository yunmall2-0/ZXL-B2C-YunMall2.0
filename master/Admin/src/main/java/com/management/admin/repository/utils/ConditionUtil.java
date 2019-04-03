/***
 * @pName proback
 * @name ConditionUtil
 * @user DF
 * @date 2018/8/4
 * @desc SQL条件工具
 */
package com.management.admin.repository.utils;

/**
 * SQL条件工具
 */
public class ConditionUtil {
    /**
     * 计算分页位置
     * @param page
     * @param limit
     * @return
     */
    public static Integer extractPageIndex(Integer page, String limit) {
        if(!limit.equalsIgnoreCase("-1")){
            page = page - 1;
            if (page != 0){
                page = page * Integer.valueOf(limit);
            }
        }
        return page;
    }

    /**
     * 模糊查询某个字段
     * @param column
     * @param value
     * @param isJoin
     * @param alias
     * @return
     */
    public static String like(String column, String value, boolean isJoin, String alias){
        String condition = "";
        if (!isJoin){
            alias = "";
        }else{
            alias += ".";
        }
        String tb = alias + "`" + column + "`";
        condition += tb + " LIKE binary  '%" + value + "' OR ";
        condition += tb + " LIKE binary  '" + value + "%' OR ";
        condition += tb + " LIKE binary  '%" + value + "%' ";
        return condition;
    }

    /**
     * 模糊查询某个字段
     * @param column
     * @param value
     * @return
     */
    public static String like(String column, String value){
        String condition = "";
        String tb =  "`" + column + "`";
        condition += tb + " LIKE binary '%" + value + "' OR ";
        condition += tb + " LIKE binary '" + value + "%' OR ";
        condition += tb + " LIKE binary '%" + value + "%' ";
        return condition;
    }


    /**
     * 准确匹配
     * @param column
     * @param value
     * @param isJoin
     * @param alias
     * @return
     */
    public static String match(String column, String value, boolean isJoin, String alias){
        String condition = "";
        if (!isJoin){
            alias = "";
        }else{
            alias += ".";
        }
        String tb = alias + "`" + column + "`";
        condition += tb + " = " + value + "  ";
        return condition;
    }

    /**
     * 提取两个日期之间的条件
     * @return
     */
    public static String extractBetweenTime(String beginTime, String endTime, String where,String column) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")){
            where += " AND "+column+" BETWEEN #{beginTime} AND #{endTime}";
        }else if (beginTime != null && beginTime.contains("-")){
            where += " AND "+column+" BETWEEN #{beginTime} AND #{endTime}";
        }else if (endTime != null && endTime.contains("-")){
            where += " AND "+column+" BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }
}
