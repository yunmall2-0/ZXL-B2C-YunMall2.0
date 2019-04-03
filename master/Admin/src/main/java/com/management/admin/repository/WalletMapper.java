/***
 * @pName management
 * @name WalletMapper
 * @user HongWei
 * @date 2018/8/16
 * @desc 用户钱包表
 */
package com.management.admin.repository;

import com.management.admin.entity.db.Wallets;
import org.apache.ibatis.annotations.*;

@Mapper
public interface WalletMapper extends MyMapper<Wallets> {

    @Update("UPDATE wallets SET balance = balance - #{amount}, update_time = NOW(), version = version + 1 WHERE user_id=#{fromUid} AND balance > 0 AND version=#{version}")
    /**
     * 扣减余额[支持负数增长] 韦德 2018年8月5日17:13:19
     * @param formUid
     * @param amount
     * @param version
     * @return
     */
    int reduceBalance(@Param("fromUid") Integer formUid, @Param("amount") Double amount, @Param("version") Integer version);

    @Update("UPDATE wallets SET balance = balance + #{amount}, update_time = NOW(), version = version + 1 WHERE user_id=#{fromUid} AND version=#{version} ")
    /**
     * 增加余额 韦德 2018年8月5日17:14:27
     * @param formUid
     * @param amount
     * @return
     */
    int addBalance(@Param("fromUid") Integer formUid, @Param("amount") Double amount, @Param("version") Integer version);



    @Update("UPDATE wallets SET balance = balance + #{amount}, update_time = NOW() WHERE user_id=#{fromUid}")
    /**
     * 增加余额 韦德 2018年8月5日17:14:27
     * @param formUid
     * @param amount
     * @return
     */
    int addBalanceByIntrgral(@Param("fromUid") Integer formUid, @Param("amount") Double amount);



    @Select("SELECT * FROM wallets WHERE user_id=#{userId}")
    /**
     * 根据uid查询钱包信息 韦德 2018年8月16日17:46:03
     * @param userId
     * @return
     */
    Wallets selectByUid(@Param("userId") Integer userId);


    @Update("UPDATE wallets SET balance = balance - #{amount}, update_time = NOW(), version = version + 1 WHERE `user_id`=#{fromUid} AND balance > 0  AND version=#{version}")
    /**
     * 扣减余额[不支持负数增长] 韦德 2018年8月5日17:13:19
     * @param formUid
     * @param amount
     * @param version
     * @return
     */

    int reduceBalanceStrict(@Param("fromUid") Integer formUid, @Param("amount") Double amount, @Param("version") Integer version);


}
