package com.management.admin.biz.impl;

import com.management.admin.biz.IWithdrawService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.db.Examine;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.db.Withdraw;
import com.management.admin.entity.param.PayParam;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.ExamineMapper;
import com.management.admin.repository.PayMapper;
import com.management.admin.repository.WithdrawMapper;
import com.management.admin.utils.web.SessionUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName WithdrawServiceImpl
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/28 17:22
 * Version 1.0
 **/
@Service
public class WithdrawServiceImpl implements IWithdrawService {

    private final WithdrawMapper withdrawMapper;

    private final PayServiceImpl payService;

    private final PayMapper payMapper;

    private final ExamineMapper examineMapper;

    @Autowired
    public WithdrawServiceImpl(WithdrawMapper withdrawMapper,PayServiceImpl payService,ExamineMapper examineMapper,PayMapper payMapper){
        this.withdrawMapper=withdrawMapper;
        this.payService=payService;
        this.examineMapper=examineMapper;
        this.payMapper=payMapper;
    }


    /**
     * 查询用户绑定的账户信息  Timor  2019-2-28 17:50:27
     * @param request
     * @return
     */
    @Override
    public String selectAccount(HttpServletRequest request) {
        Integer userId=SessionUtil.getSession(request).getUserId();
        Withdraw withdraw=withdrawMapper.selectWithdraw(userId);
        if(withdraw!=null){
            return  withdraw.getCashAccount();
        }
        return null;
    }

    /**
     * 绑定账户信息 Timor  2019-2-28 17:24:09
     * @param request
     * @param withdraw
     * @return
     */
    @Override
    @Transactional
    public Integer  bindAccount(HttpServletRequest request,Withdraw withdraw) {
        Integer userId=SessionUtil.getSession(request).getUserId();
        withdraw.setUserId(userId);
        Withdraw draw=withdrawMapper.selectWithdraw(userId);
        if(draw==null){
            return withdrawMapper.insertWithdraw(withdraw);
        }else {
            return  withdrawMapper.updateWithdrawId(withdraw);
        }
    }

    /**
     * 提现   Timor   2019-3-1 16:05:13
     * @param request
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public Integer withdraw(HttpServletRequest request, Double amount) {

        /*判断账户是否存在*/
        Integer userId=SessionUtil.getSession(request).getUserId();
        Withdraw withdraw=withdrawMapper.selectWithdraw(userId);
        if(withdraw!=null){
            Integer count = 0;
            /*生成交易流水*/
            PayParam payParam=new PayParam();
            payParam.setFromUid(userId);
            payParam.setToUid(Constant.SYSTEM_ACCOUNTS_ID);
            payParam.setAmount(amount);
            Long systemRecordId=payService.withdraw(payParam);
            /*生成审批记录*/
            Examine examine=new Examine();
            examine.setUserId(userId);
            examine.setSystemRecordId(systemRecordId);
            examine.setRealName(withdraw.getRealName());
            examine.setCashAccount(withdraw.getCashAccount());
            examine.setMoney(amount);
            count=examineMapper.insertExamine(examine);
            if(count == 0) throw new MsgException("添加审批记录失败");
            return count;
        }
        return -1;
    }


    /**
     * 通过审核   Timor  2019-3-1 16:44:05
     * @param shenpiId
     * @param channelRecordId
     * @return
     */
    @Override
    @Transactional
    public Integer passCheck(Integer shenpiId,String channelRecordId) {

        // 1、更新提现审批表
        Examine model = examineMapper.selectById(shenpiId);
        if(model == null) throw new MsgException("审批请求不存在");
        Integer count=0;
        count=examineMapper.updateStatus(shenpiId,channelRecordId);
        if(count == 0) throw new MsgException("编辑提现状态失败");
        // 2、更新财务日志
        count = 0;
        count = payMapper.updateChannelRecordId(model.getSystemRecordId(),channelRecordId);
        if(count == 0) throw new MsgException("更新交易回执失败");
        return count;
    }


    /**
     * 审核拒绝  Timor   2019-3-1 17:02:37
     * @param shenpiId
     * @param remake
     * @return
     */
    @Override
    @Transactional
    public Integer auditFailure(Integer shenpiId,String  remake) {


        Examine withdraw = examineMapper.selectById(shenpiId);
        if(withdraw == null) throw new MsgException("审批请求不存在");
        /*修改审批记录为已拒绝*/
        Integer result=examineMapper.updateStatusFail(shenpiId,remake);
        if(result==0) throw new MsgException("编辑提现状态失败");
        /*提现金额返还用户生成交易流水*/
        PayParam payParam=new PayParam();
        payParam.setFromUid(withdraw.getUserId());
        payParam.setToUid(Constant.SYSTEM_ACCOUNTS_ID);
        payParam.setAmount(withdraw.getMoney());
        payParam.setRemark("提现失败，金额退回钱包");
        payService.refund(payParam);
        return result;
    }
}
