/***
 * @pName management
 * @name PayService
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.biz;

import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.db.Wallets;
import com.management.admin.entity.dbExt.OrdersDatails;
import com.management.admin.entity.param.PayParam;
import com.management.admin.entity.resp.AccountsResp;
import com.management.admin.entity.resp.UserResp;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IPayService extends IBaseService<Pays> {



    /**
     * 转账 韦德 2018年8月16日13:17:17
     * @param payParam
     */
    void transfer(PayParam payParam);

    /**
     * 批量转账 韦德 2018年8月21日01:53:25
     * @param payParams
     */
    void batchTransfer(List<PayParam> payParams);

    /**
     * 提现 韦德 2018年8月21日13:42:56
     * @param payParam
     */
    Long withdraw(PayParam payParam);


    /**
     * 退款  Timor  2019-3-2 13:40:42
     * @param payParam
     */
    void refund(PayParam payParam);

    /**
     * 消费 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    void consume(PayParam payParam);

    /**
     * 消费 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    Pays consumeReturn(PayParam payParam);
    /**
     * 批量消费 韦德 2018年8月21日01:53:25
     * @param payParams
     */
    void batchConsume(List<PayParam> payParams);

    /**
     * 充值 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    Long recharge(PayParam payParam);

    /**
     * 获取不可用余额 韦德 2018年8月21日15:50:04
     * @param userId
     * @return
     */
    Double getNotWithdrawAmount(Integer userId);

    /**
     * 获取可用余额 韦德 2018年8月21日15:50:04
     * @param userId
     * @return
     */
    Double getWithdrawAmount(Integer userId);

    /**
     * 分页加载财务会计账目数据列表 韦德 2018年8月27日00:20:54
     * @param page
     * @param limit
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    List<Accounts> getAccountsLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end);

    /**
     * 统计分页加载财务会计账目数据列表
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    int getAccountsLimitCount(String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end);

    /**
     * 加载财务会计账目数据总条数 韦德 2018年8月27日00:21:16
     * @return
     */
    int getAccountsCount();

    /**
     * 统计系统账户余额以及交易额 韦德 2018年8月27日11:21:35
     * @param systemAccountsId
     * @return
     */
    UserResp getAccountAmount(Integer systemAccountsId);

    /**
     * 取账户的总收入与总支出情况的实时数据 韦德 2018年8月7日00:43:31
     * @param id
     * @return
     */
    UserResp getAccountAmountForDB(Integer id);

    /**
     * 获取账户的总收入与总支出情况的缓存 韦德 2018年8月7日00:43:31
     * @param id
     * @return
     */
    UserResp getAccountAmountForCache(Integer id);

    /**
     * 分页加载交易流水 韦德 2018年8月27日21:55:17
     * @param page
     * @param limit
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    List<Pays> getPaysLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end);

    /**
     * 加载交易流水数据总条数 韦德 2018年8月27日21:55:32
     * @return
     */
    int getPaysCount();

    /**
     * 加载交易流水数据分页总条数 韦德 2018年8月27日22:37:10
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    Integer getPaysLimitCount(String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end);

    /**
     * 充值 韦德 2018年8月7日03:05:53
     * @param id
     * @param amount
     */
    Boolean recharge(Integer id, Double amount);

    /**
     * 更新交易回执单号 韦德 2018年8月30日14:54:32
     * @param systemRecordId
     * @param channelRecordId
     * @return
     */
    int updateChannelRecordId(Long systemRecordId, String channelRecordId);



    /**
     * 分页查询用户流水 Timor 2019-2-27 10:46:02
     * @param page
     * @param limit
     * @param condition
     * @param userId
     * @return
     */
    List<Pays> selectPaysByPages(Integer page, String limit, String condition, Integer  userId,String trade_date_begin, String trade_date_end);


    /**
     * 分页用户流水记录数 Timor 2019-2-27 10:46:12
     * @param condition
     * @param userId
     * @return
     */
    Integer getPageCount(String condition, Integer userId,String trade_date_begin, String trade_date_end);

    /**
     * 本项目的用户消费 钉子 2019年2月27日16:43:30
     * @param userId
     * @param money
     * @return
     */
    Pays getMoney(Integer userId,Double money,Integer status);

    /**
     * 查询用户相应类型下的消费总额  Timor  2019-2-27 16:36:36
     * @param request
     * @param tradeType
     * @return
     */
    Double queryConsumeByType(HttpServletRequest request,Integer tradeType);


    /**
     * 本项目的用户充值  钉子 2019年2月27日16:44:26
     * @param userId
     * @param money
     * @return
     */
    Integer giveMoney(Integer userId,Double money);
    /**
     * 本项目的用户提现  钉子 2019年2月27日16:44:26
     * @param userId
     * @param money
     * @return
     */
    Integer withMoney(Integer userId,Double money);

    /**
     * 消费 钉子 2019年2月28日10:38:00
     *
     * @param payParam
     */
    Pays aliConsume(PayParam payParam);

    /**
     * 提现 钉子  2019年2月28日10:37:55
     * @param payParam
     */
    Long aliRecharge(PayParam payParam);
}
