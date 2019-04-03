package com.management.admin.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.management.admin.annotaion.FinanceToken;
import com.management.admin.biz.IPayService;
import com.management.admin.biz.IUserService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.resp.AccountsResp;
import com.management.admin.entity.resp.PaysResp;
import com.management.admin.entity.resp.UserResp;
import com.management.admin.exception.FinanceException;
import com.management.admin.utils.PropertyUtil;
import com.management.admin.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "finance")
@Controller
public class FinanceController {
    @Autowired
    private IPayService iPayService;
    @Autowired
    private IUserService iUserService;

    /**
     * 财务会计账目页面  钉子 2019年2月22日13:30:43
     * @return
     */
    @RequestMapping(value = "getAccountPage")
    public String getAccountPage(){
        return "finance/accounts/index";
    }

    /**
     * 分页查询财务会计账目页面  钉子 2019年2月22日13:35:45
     * @param page
     * @param limit
     * @param condition
     * @param trade_type
     * @param filter_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    @RequestMapping(value = "getAccountLimit")
    @ResponseBody
    public JsonArrayResult<AccountsResp> getAccountsLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end){
        Integer count = 0;
        if(condition != null && condition.contains("[add]")) {
            condition = condition.replace("[add]", "+");
        }
        if(condition != null && condition.contains("[reduce]")) {
            condition = condition.replace("[reduce]", "-");
        }
        List<Accounts> list = iPayService.getAccountsLimit(page, limit, condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        List<AccountsResp> wrapList = new ArrayList<>();
        list.forEach(item -> {
            AccountsResp resp = new AccountsResp();
            PropertyUtil.clone(item, resp);
            resp.setAccountsId(item.getAccountsId().toString());
            resp.setPayId(item.getPayId().toString());
            wrapList.add(resp);
        });
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0,wrapList);
        if (StringUtil.isBlank(condition)
                && StringUtil.isBlank(trade_date_begin)
                && StringUtil.isBlank(trade_date_end)
                && (trade_type == null || trade_type == 0)){
            count = iPayService.getAccountsCount();
        }else{
            count = iPayService.getAccountsLimitCount(condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 跳转财务交易流水页面  钉子 2019年2月22日13:38:09
     * @return
     */
    @RequestMapping(value = "payPage")
    public String payPage(){
        return "finance/pay/index";
    }
    /**
     * 分页查询交易流水 钉子  2019年2月22日13:39:13
     * @param condition
     * @return
     */
    @RequestMapping(value = "getPayLimit")
    @ResponseBody
    public JsonArrayResult<PaysResp> getPayLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end){
        Integer count = 0;
        List<Pays> list = iPayService.getPaysLimit(page, limit, condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        List<PaysResp> wrapList = new ArrayList<>();
        list.forEach(item -> {
            PaysResp resp = new PaysResp();
            PropertyUtil.clone(item, resp);
            resp.setPayId(item.getPayId().toString());
            resp.setSystemRecordId(item.getSystemRecordId().toString());
            wrapList.add(resp);
        });
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0,wrapList);
        if (StringUtil.isBlank(condition)
                && StringUtil.isBlank(trade_date_begin)
                && StringUtil.isBlank(trade_date_end)
                && (trade_type == null || trade_type == 0)){
            count = iPayService.getPaysCount();
        }else{
            count = iPayService.getPaysLimitCount(condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 查询系统账户信息 韦德 2018年8月27日21:53:30
     * @return
     */
    @RequestMapping(value = "getSystemAccount")
    @ResponseBody
    @JsonView(UserResp.FinanceView.class)
    public UserResp getSystemAccount(){
        UserResp userDetail = iUserService.getUsersById(Constant.SYSTEM_ACCOUNTS_ID);
        if(userDetail != null) {
            UserResp accountAmount = iPayService.getAccountAmount(Constant.SYSTEM_ACCOUNTS_ID);
            if(accountAmount != null) {
                userDetail.setIncomeAmount(accountAmount.getIncomeAmount());
                userDetail.setExpendAmount(accountAmount.getExpendAmount());
            }
        }
        return userDetail;
    }

    /**
     * 跳转充值页面  钉子 2019年2月22日13:56:33
     * @return
     */
    @RequestMapping( value = "recharge")
    public String recharge(){
        return "finance/pay/recharge";
    }

    /**
     * 人工充值  钉子 2019年2月22日13:57:42
     * @param username
     * @param amount
     * @return
     */
    @FinanceToken
    @RequestMapping(value = "toRecharge")
    @ResponseBody
    public JsonResult recharge(String username, Double amount) {
        Users users = iUserService.getUserByUserName(username);
        if(users == null) {
            throw new FinanceException(FinanceException.Errors.NOT_FOUND_USER,"用户不存在");
        }
        if(iPayService.recharge(users.getUserId(), amount)) {
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

    /**
     * 无流水充值  钉子 2019年2月22日13:58:16
     * @param username
     * @param amount
     * @return
     */
    @FinanceToken
    @RequestMapping(value = "changeBalance")
    @ResponseBody
    public JsonResult changeBalance(String username, Double amount) {
        iUserService.changeBalance(username, amount);
        return JsonResult.successful();
    }
}
