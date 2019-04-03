package com.management.admin.biz.impl;

import com.management.admin.biz.IExamineService;
import com.management.admin.entity.db.Examine;
import com.management.admin.entity.param.PayParam;
import com.management.admin.repository.ExamineMapper;
import com.management.admin.repository.utils.ConditionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName examineServiceImpl
 * @Description
 * @Author ZXL01
 * @Date 2019/1/10 20:22
 * Version 1.0
 **/

@Service
public class ExamineServiceImpl implements IExamineService {

    private  final ExamineMapper examineMapper;
    private  final PayServiceImpl payService;
    @Autowired
    public ExamineServiceImpl(ExamineMapper examineMapper, PayServiceImpl payService){
        this.examineMapper=examineMapper;
        this.payService=payService;
    }

    /**
     * 提取分页条件
     * @return
     */
    private String extractLimitWhere(String condition, String beginTime, String endTime) {
        // 查询模糊条件
        String where = " 1=1 ";
        if(condition != null) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("shenpi_id", condition, false , "t1");
            if (condition.split("-").length == 2){
                where += " OR " + ConditionUtil.like("create_time", condition, false  , "t1");

            }
            if(condition=="审核中"){
                where += " OR " + ConditionUtil.like("status", "0", false  , "t1");
            }
            if(condition=="审核成功"){
                where += " OR " + ConditionUtil.like("status", "1", false  , "t1");
            }
            if(condition=="审核失败"){
                where += " OR " + ConditionUtil.like("status", "2", false  , "t1");
            }
            where += " OR " + ConditionUtil.like("cash_account", condition, false  , "t1");
            where += " OR " + ConditionUtil.like("channel_record_id", condition, false  , "t1");
            where += " OR " + ConditionUtil.like("system_record_id", condition, false  , "t1");
            where += " OR " + ConditionUtil.like("real_name", condition, false   , "t1")  + ")";
        }
        // 取两个日期之间或查询指定日期
        where = extractBetweenTime(beginTime, endTime, where);
        return where.trim();
    }
    /**
     * 提取两个日期之间的条件
     * @return
     */
    private String extractBetweenTime(String beginTime, String endTime, String where) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")){
            where += " AND create_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (beginTime != null && beginTime.contains("-")){
            where += " AND create_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (endTime != null && endTime.contains("-")){
            where += " AND create_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }



    /**
     * 查询所有的用户基本信息 提莫 2018-11-29 10:46:45
     * @return List<Users>
     */
    @Override
    public List<Examine> selectAllExamine(Integer page, String limit, String condition, String beginTime, String endTime) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractLimitWhere(condition, beginTime, endTime);
        List<Examine> list = examineMapper.selectLimit(page, limit,beginTime, endTime, where);
        return list;
    }

    /**
     * 加载分页记录数 韦德 2018年8月30日11:29:22
     *
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer getLimitCount(String condition, String beginTime, String endTime) {
        String where = extractLimitWhere(condition, beginTime, endTime);
        return examineMapper.getPaysLimitCount(beginTime, endTime, where);
    }

    /**
     * 加载总记录数 韦德 2018年8月30日11:29:11
     *
     * @return
     */
    @Override
    public Integer getCount() {
        return examineMapper.getCount();
    }

    /**
     * 添加余额审批记录
     */
    @Override
    public Integer insertExamine(Examine examine) {
        return examineMapper.insertExamine(examine);
    }


}
