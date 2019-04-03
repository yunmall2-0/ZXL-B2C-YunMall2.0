/***
 * @pName management
 * @name BaseController
 * @user DF
 * @date 2018/8/30
 * @desc
 */
package com.management.admin.controller;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseController {
    @InitBinder
    public void init(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    /**
     * 输出信息到页面
     * @param response
     * @param msg
     */
    public void printOutMsg(HttpServletResponse response, String msg){
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.print(msg);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取请求完整路径
     * @param request
     * @return
     */
    public String getUrl(HttpServletRequest request){
        String url = request.getRequestURI();
        String params = "";
        if(request.getQueryString()!=null){
            params = request.getQueryString().toString();
        }
        if(!"".equals(params)){
            url = url+"?"+params;
        }
        return url;
    }

    /**
     * 获取日期
     * @param day   天
     */
    public String getDate(int day){
        StringBuffer s = new StringBuffer();
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DATE);
        if(day < 0){
            c.add(Calendar.YEAR, -1);
            c.set(Calendar.DATE, currentDay);
        }else if(day == 29){
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DATE, currentDay);
        }else{
            c.add(Calendar.DATE, -day);
        }

        s.append(c.get(Calendar.YEAR)+"-");
        s.append((c.get(Calendar.MONTH)+1) < 10 ? ("0"+(c.get(Calendar.MONTH)+1)) : (c.get(Calendar.MONTH)+1));
        s.append("-");
        s.append(c.get(Calendar.DATE) < 10 ? ("0"+c.get(Calendar.DATE)) : c.get(Calendar.DATE));
        return s.toString();
    }

    /**
     * 转换统计的map
     * @param statMap       统计的map
     * @param constMap      常量的map
     * @return
     */
    public Map<String, Long> getFmtMap(Map<String, Long> statMap, Map<Integer, String> constMap){
        Map<String, Long> dataMap = null;
        if(statMap != null){
            dataMap = new LinkedHashMap<>();
            for(Map.Entry<String, Long> entry : statMap.entrySet()){
                dataMap.put(constMap.get(Integer.valueOf(entry.getKey()))+"&"+Integer.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return dataMap;
    }

}
