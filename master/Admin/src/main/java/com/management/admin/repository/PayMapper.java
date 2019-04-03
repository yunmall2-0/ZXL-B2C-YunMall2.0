/***
 * @pName management
 * @name PayMapper
 * @user HongWei
 * @date 2018/8/16
 * @desc 财务交易记录表
 */
package com.management.admin.repository;

import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.dbExt.OrdersDatails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PayMapper extends  MyMapper<Pays>{
    @Select("SELECT \n" +
            "COALESCE(SUM(amount),0) \n" +
            "- \n" +
            "(SELECT COALESCE(SUM(amount),0) FROM `accounts` WHERE trade_account_id = #{userId} AND currency = 1 AND accounts_type = 2)\n" +
            "FROM `accounts` WHERE trade_account_id = #{userId} AND accounts_type = 1 AND currency = 1 \n")
    //@Select("SELECT sum(amount) FROM tb_pays WHERE to_uid = #{userId} AND currency = 1 AND (remark IS NULL OR remark != #{remark})")
    /**
     * 统计不可提现金额 韦德 2018年8月20日18:43:47
     * @param userId
     * @param remark
     * @return
     */
    Double selectNotWithdrawAmount(@Param("userId") Integer userId, @Param("remark") String remark);


    @Select("SELECT  after_balance - (" +
            "SELECT  COALESCE(SUM(amount),0)  - (" +
            " SELECT COALESCE(SUM(amount),0) FROM `accounts` WHERE trade_account_id = #{userId} AND currency = 1 AND accounts_type = 2) " +
            "   FROM `accounts` WHERE trade_account_id = #{userId} AND accounts_type = 1 AND currency = 1 " +
            ") FROM `accounts` WHERE trade_account_id = #{userId} ORDER BY accounts_id DESC LIMIT 1;")
    /**
     * 统计可提现金额 韦德 2018年8月21日11:17:17
     * @param userId
     * @return
     */
    Double selectWithdrawAmount(@Param("userId") Integer userId);

    @Select("SELECT t1.*,t2.user_name,t3.* FROM pays t1 LEFT JOIN users t2 ON t1.from_uid = t2.user_id " +
            " LEFT JOIN pays t3 ON t1.to_uid = t2.user_id " +
            "WHERE ${condition} ORDER BY t1.add_time DESC LIMIT #{page},${limit}")
    /**
     * 查询分页 韦德 2018年8月27日00:39:38
     * @param page
     * @param limit
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    List<Pays> selectLimit(@Param("page") Integer page, @Param("limit") String limit
            , @Param("trade_type") Integer trade_type
            , @Param("beginTime") String trade_date_begin
            , @Param("endTime") String trade_date_end
            , @Param("condition") String condition);

    @Select("SELECT COUNT(t1.pay_id) FROM pays t1 LEFT JOIN users t2 ON t1.from_uid = t2.user_id " +
            "LEFT JOIN pays t3 ON t1.to_uid = t2.user_id " +
            "WHERE ${condition}")
    /**
     * 查询分页记录总数 韦德 2018年8月27日09:52:40
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    int getPaysLimitCount(@Param("trade_type") Integer tradeType
            , @Param("beginTime") String trade_date_begin
            , @Param("endTime") String trade_date_end
            , @Param("condition") String condition);

    @Update("UPDATE pays SET channel_record_id=#{channelRecordId},to_account_time=NOW() WHERE system_record_id=#{systemRecordId}")
    /**
     * 更新交易回执单号 韦德  2018年8月30日14:55:50
     * @param systemRecordId
     * @param channelRecordId
     * @return
     */
    int updateChannelRecordId(@Param("systemRecordId") Long systemRecordId, @Param("channelRecordId") String channelRecordId);


    /**
     * 根据交易类型查询用户的流水信息 Timor   2019-2-27 16:30:43
     * @param tradeType
     * @param userId
     * @return
     */
    @Select("select *  from pays  where from_uid=#{userId}  and trade_type=#{tradeType} ORDER BY add_time DESC")
    List<Pays> selectPaysByType(@Param("tradeType") Integer tradeType,@Param("userId") Integer userId);

    /**
     * 分页查询用户流水  Timor 2019-2-27 10:41:43
     * @param limit
     * @param page
     * @param userId
     * @param condition
     * @return
     */
    @Select("select *  from pays  where ${condition} and from_uid=#{userId} ORDER BY add_time DESC LIMIT #{page},${limit}")
    List<Pays> selectPaysPage(@Param("limit") String limit, @Param("page") Integer page,
                              @Param("beginTime") String trade_date_begin,
                              @Param("endTime") String trade_date_end,
                              @Param("userId") Integer userId,
                              @Param("condition") String condition);


    /**
     * 查询分页(用户)流水记录总数 Timor 2019-2-27 10:43:59
     * @param userId
     * @param condition
     * @return
     */
    @Select("select count(pay_id) from pays where ${condition} and from_uid=#{userId}")
    Integer selectPaysCount(@Param("condition") String condition,
                            @Param("beginTime") String trade_date_begin,
                            @Param("endTime") String trade_date_end,
                            @Param("userId") Integer userId);


    /**
     * 提现审批修改备注为已通过   Timor   2019-3-1 16:54:08
     * @param systemRecordId
     * @return
     */
    @Update("update pays set remark='审核已通过' where system_record_id=#{systemRecordId}")
    Integer updatePaysRemark(Long systemRecordId);

}
