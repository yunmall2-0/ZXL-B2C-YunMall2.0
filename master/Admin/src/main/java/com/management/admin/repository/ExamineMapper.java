package com.management.admin.repository;

import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Examine;
import com.management.admin.entity.db.Pays;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamineMapper extends MyMapper<Examine>  {

    @Select("SELECT   *  FROM examine  " +
            " WHERE ${condition} ORDER BY create_time DESC LIMIT #{page},${limit}")
    /**
     * 查询分页 Timor 2018年8月27日00:39:38
     * @param page
     * @param limit
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    List<Examine> selectLimit(@Param("page") Integer page, @Param("limit") String limit
            , @Param("beginTime") String begin
            , @Param("endTime") String end
            , @Param("condition") String condition);



    @Select("SELECT COUNT(shenpi_id) FROM examine  WHERE ${condition}")
    /**
     * 查询分页记录总数 韦德 2018年8月27日09:52:40
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    int getPaysLimitCount(
            @Param("beginTime") String begin
            , @Param("endTime") String end
            , @Param("condition") String condition);

    /**
     * 查询总记录数
     */
    @Select("Select COUNT(*) from examine")
    Integer getCount();

    /**
     * 查询审批记录
     * @param shenpiId
     * @return
     */
    @Select("Select * from examine where shenpi_id=#{shenpiId}")
    Examine selectById(Integer shenpiId);


    /**
     * 添加余额提现审批记录
     */
    @Insert("insert into examine(user_id,cash_account,real_name,money,system_record_id,create_time,update_time,remark) values " +
            " (#{userId},#{cashAccount},#{realName},#{money},#{systemRecordId},NOW(),NOW(),#{remark})")
    Integer insertExamine(Examine examine);


    /**
     * 修改审批状态为已审核
     */
    @Update("update examine  set status=1,channel_record_id=#{channelRecordId},update_time=NOW() where shenpi_id=#{shenpiId}")
    Integer updateStatus(@Param("shenpiId") Integer shenpiId,@Param("channelRecordId") String channelRecordId);


    /**
     * 修改审批状态为审核失败
     */
    @Update("update examine  set status=2 ,remark=#{remark}  where shenpi_id=#{shenpiId}")
    Integer updateStatusFail(@Param("shenpiId") Integer shenpiId, @Param("remark") String remark);

}
