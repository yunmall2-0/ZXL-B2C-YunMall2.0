package com.management.admin.biz;

import com.management.admin.entity.db.Withdraw;

import javax.servlet.http.HttpServletRequest;

public interface IWithdrawService {


    /**
     * 绑定用户账户信息 Timor  2019-2-28 17:22:26
     * @param  request
     * @param withdraw
     * @return
     */
    Integer bindAccount(HttpServletRequest request,Withdraw withdraw);


    /**
     * 查询账户信息
     * @param request
     * @return
     */
    String selectAccount(HttpServletRequest request);


    /**
     * 提现     Timor   2019-3-1 16:04:20
     * @param request
     * @param amount
     * @return
     */
    Integer withdraw(HttpServletRequest request,Double amount);


    /**
     * 提现审核通过  Timor   2019-3-1 16:43:32
     * @param shenpiId
     * @param channelRecordId
     * @return
     */
    Integer passCheck(Integer shenpiId,String channelRecordId);



    /**
     * 提现审核拒绝   Timor   2019-3-1 17:01:59
     * @param shenpiId
     * @param remake
     * @return
     */
    Integer auditFailure(Integer shenpiId,String  remake);

}
