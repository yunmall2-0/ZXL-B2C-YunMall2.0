package com.management.admin.biz;

import com.management.admin.entity.db.Examine;

import java.util.List;

public interface IExamineService {


    /**
     * 查询所有的用户提现审批信息 提莫 2018-11-29 10:39:45
     * @return List<Users>
     */
    List<Examine> selectAllExamine(Integer page, String limit, String condition, String beginTime, String endTime);


    /**
     * 加载分页记录数 韦德 2018年8月30日11:29:22
     *
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer getLimitCount(String condition, String beginTime, String endTime);

    /**
     * 加载总记录数 韦德 2018年8月30日11:29:11
     *
     * @return
     */
    Integer getCount();


    /**
     * 添加余额审批记录
     */

    Integer insertExamine(Examine examine);

}
