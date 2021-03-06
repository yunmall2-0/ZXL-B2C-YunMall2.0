/***
 * @pName management
 * @name AccountMapper
 * @user HongWei
 * @date 2018/8/16
 * @desc 财务会计账目表
 */
package com.management.admin.repository;

import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.resp.AccountsResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper extends MyMapper<Accounts> {
    @Insert("<script>" +
            "INSERT INTO `accounts` (`accounts_id`,`pay_id`, `trade_account_id`, `trade_account_name`, `accounts_type`, `amount`, `before_balance`, `after_balance`, `add_time`, `remark`, `currency`) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.accountsId},#{item.payId}, #{item.tradeAccountId}, #{item.tradeAccountName}, #{item.accountsType}, #{item.amount}" +
            ",(SELECT CASE WHEN #{item.accountsType} = 1 THEN  balance - #{item.amount}  ELSE balance + #{item.amount}  END AS balance FROM wallets WHERE user_id = #{item.tradeAccountId})" +
            ",(SELECT balance FROM wallets WHERE user_id = #{item.tradeAccountId})" +
            ", NOW(), #{item.remark}, #{item.currency})" +
            "</foreach>" +
            "</script>")
    /**
     * 批量插入 韦德 2018年8月16日15:42:01
     * @param list
     * @return
     */
    int insertList(@Param("list") List<Accounts> list);




    @Select("SELECT t1.*,t2.user_name FROM accounts t1 LEFT JOIN users t2 ON t1.trade_account_id = t2.user_id " +
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
    List<Accounts> selectLimit(@Param("page") Integer page, @Param("limit") String limit
            , @Param("accounts_type") Integer accountsType
            , @Param("beginTime") String trade_date_begin
            , @Param("endTime") String trade_date_end
            , @Param("condition") String condition);


    @Select("SELECT COUNT(t1.accounts_id) FROM accounts t1 LEFT JOIN  users t2 ON t1.trade_account_id = t2.user_id " +
            "WHERE ${condition}")
    /**
     * 查询分页记录总数 韦德 2018年8月27日09:52:40
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    int selectLimitCount(@Param("accounts_type") Integer accountsType
            , @Param("beginTime") String trade_date_begin
            , @Param("endTime") String trade_date_end
            , @Param("condition") String condition);



    @Select("SELECT (SELECT SUM(amount) FROM pays WHERE from_uid != #{id}) AS incomeAmount\n" +
            "\t\t\t\t, (SELECT SUM(amount) FROM pays WHERE from_uid = #{id}) AS expendAmount FROM pays LIMIT 1;")
    /**
     * 获取系统账户账簿 韦德 2018年8月27日17:38:27
     * @param id
     * @return
     */
    Map<String, BigDecimal> getSystemAccounts(@Param("id") Integer id);


}
