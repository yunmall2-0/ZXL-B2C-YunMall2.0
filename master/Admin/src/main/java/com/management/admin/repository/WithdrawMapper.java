package com.management.admin.repository;

import com.management.admin.entity.db.Withdraw;
import org.apache.ibatis.annotations.*;

@Mapper
public interface WithdrawMapper extends MyMapper<Withdraw> {


    /**
     * 绑定用户提现账户 Timor  2019-2-28 17:12:52
     * @param withdraw
     * @return
     */
    @Insert("insert into withdraw(user_id,cash_account,real_name,add_time) values (#{userId},#{cashAccount},#{realName},NOW())")
    Integer insertWithdraw(Withdraw withdraw);

    /**
     * 更改用户账户信息 Timor  2019-2-28 17:19:39
     * @param withdraw
     * @return
     */
    @Update("update withdraw set cash_account=#{cashAccount},real_name=#{realName} where user_id=#{userId}")
    Integer updateWithdrawId(Withdraw withdraw);

    /**
     * 查询用户绑定的账户信息 Timor  2019-2-28 17:49:11
     * @param userId
     * @return
     */
    @Select("select *  from withdraw where user_id=#{userId}")
    Withdraw selectWithdraw(Integer userId);

}
