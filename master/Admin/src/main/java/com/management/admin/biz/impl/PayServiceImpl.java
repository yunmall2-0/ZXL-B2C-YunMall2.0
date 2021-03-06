package com.management.admin.biz.impl;

import com.alibaba.fastjson.JSON;
import com.management.admin.annotaion.AspectLog;
import com.management.admin.biz.IPayService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.DataDictionary;
import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.dbExt.OrdersDatails;
import com.management.admin.entity.dbExt.UserDetailInfo;
import com.management.admin.entity.param.PayParam;
import com.management.admin.entity.resp.UserResp;
import com.management.admin.exception.InfoException;
import com.management.admin.repository.*;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.utils.DecimalUtil;
import com.management.admin.utils.IdWorker;
import com.management.admin.utils.JsonUtil;
import com.management.admin.utils.web.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PayServiceImpl extends BaseServiceImpl<Pays> implements IPayService {

    private final PayMapper payMapper;
    private final AccountMapper accountMapper;
    private final WalletMapper walletMapper;
    private final UsersMapper usersMapper;
    private final DictionaryMapper dictionaryMapper;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    public PayServiceImpl(PayMapper payMapper, AccountMapper accountMapper, WalletMapper walletMapper, UsersMapper usersMapper, DictionaryMapper dictionaryMapper) {
        this.payMapper = payMapper;
        this.accountMapper = accountMapper;
        this.walletMapper = walletMapper;
        this.usersMapper = usersMapper;
        this.dictionaryMapper = dictionaryMapper;
    }

    /**
     * 转账 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    @Transactional
    public void transfer(PayParam payParam) {
// 查询交易主体信息
        List<UserDetailInfo> userDetailInfoList = this.getUsersInfo(payParam);
        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(fromUserInfo.getBalance() <= 0) throw new InfoException("甲方钱包余额不足");

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractTransferPayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) throw new InfoException("生成交易流水失败");


        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalance(fromUserInfo.getUserId(), payParam.getAmount(),fromUserInfo.getVersion());
        if(count == 0) throw new InfoException("甲方账户扣款失败");

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) throw new InfoException("乙方账户加款失败");


        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) throw new InfoException("生成往来账失败");

        System.out.println("交易成功");
    }

    /**
     * 批量转账 韦德 2018年8月21日01:53:25
     *
     * @param payParams
     */
    @Override
    @Transactional
    public void batchTransfer(List<PayParam> payParams) {
        payParams.forEach(payParam -> {
            this.transfer(payParam);
        });
    }

    /**
     * 提现 韦德 2018年8月21日13:42:56
     *
     * @param payParam
     */
    @Override
    @Transactional
    public Long withdraw(PayParam payParam) {
        payParam.setToUid(Constant.SYSTEM_ACCOUNTS_ID);

        // 查询交易主体信息
        String userId = payParam.getFromUid().toString().concat(",").concat(payParam.getToUid().toString());
        List<UserDetailInfo> userDetailInfoList = usersMapper.selectInUid(userId);
        if(userDetailInfoList.isEmpty() || userDetailInfoList.size() != 2) {
            throw new InfoException("查询交易主体账户信息不完整");
        }

        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(fromUserInfo.getBalance() <= 0) throw new InfoException("甲方钱包余额不足");

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractWithdrawPayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) {
            throw new InfoException("生成交易流水失败");
        }

        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalanceStrict(fromUserInfo.getUserId(), payParam.getAmount(),fromUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("甲方账户扣款失败");
        }

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("乙方账户加款失败");
        }


        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) throw new InfoException("生成往来账失败");

        System.out.println("交易成功");

        return payParam.getSystemRecordId();
    }


    /**
     * 退款(提现失败金额退回用户钱包) Timor  2019-3-2 13:42:39
     * @param payParam
     */
    @Override
    @Transactional
    public void refund(PayParam payParam) {
        payParam.setToUid(Constant.SYSTEM_ACCOUNTS_ID);

        // 查询交易主体信息
        String userId = payParam.getFromUid().toString().concat(",").concat(payParam.getToUid().toString());
        List<UserDetailInfo> userDetailInfoList = usersMapper.selectInUid(userId);
        if(userDetailInfoList.isEmpty() || userDetailInfoList.size() != 2) {
            throw new InfoException("查询交易主体账户信息不完整");
        }

        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(toUserInfo.getBalance() <= 0) throw new InfoException("公户钱包余额不足");

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractRefundPayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) {
            throw new InfoException("生成交易流水失败");
        }

        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalanceStrict(toUserInfo.getUserId(), payParam.getAmount(),toUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("公户账户扣款失败");
        }

        count = 0;
        count = walletMapper.addBalance(fromUserInfo.getUserId(), payParam.getAmount(), fromUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("甲方账户加款失败");
        }

        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) throw new InfoException("生成往来账失败");

        System.out.println("交易成功");
    }

    /**
     * 消费 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    @Transactional
    @AspectLog(description = "WC转账")
    public void consume(PayParam payParam) {
        consumeReturn(payParam);
    }
    /**
     * 消费 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    public Pays consumeReturn(PayParam payParam) {
        // 查询交易主体信息
        List<UserDetailInfo> userDetailInfoList = this.getUsersInfo(payParam);
        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(fromUserInfo.getBalance() <= 0) {
            throw new InfoException("甲方钱包余额不足");
        }

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractConsumePayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) {
            throw new InfoException("生成交易流水失败");
        }


        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalance(fromUserInfo.getUserId(), payParam.getAmount(),fromUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("甲方账户扣款失败");
        }

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("乙方账户加款失败");
        }

        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) {
            throw new InfoException("生成往来账失败");
        }
        System.out.println("交易成功");
        return pays;
    }



    /**
     * 批量消费 韦德 2018年8月21日01:53:25
     *
     * @param payParams
     */
    @Override
    @Transactional
    public void batchConsume(List<PayParam> payParams) {
        payParams.forEach(payParam -> {
            this.consume(payParam);
        });
    }

    /**
     * 充值 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    @Transactional
    public Long recharge(PayParam payParam) {
        logger.info("转账参数:" + JsonUtil.getJson(payParam));

        // 查询交易主体信息
        List<UserDetailInfo> userDetailInfoList = this.getUsersInfo(payParam);
        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(fromUserInfo.getBalance() <= 0) throw new InfoException("甲方钱包余额不足");

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractRechargePayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) throw new InfoException("生成交易流水失败");


        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalance(fromUserInfo.getUserId(), payParam.getAmount(),fromUserInfo.getVersion());
        if(count == 0) throw new InfoException("甲方账户扣款失败");

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) throw new InfoException("乙方账户加款失败");


        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) throw new InfoException("生成往来账失败");

        System.out.println("交易成功");
        return payParam.getSystemRecordId();
    }


    /**
     * 获取不可用余额 韦德 2018年8月21日15:50:04
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Double getNotWithdrawAmount(Integer userId) {
        return payMapper.selectNotWithdrawAmount(userId, "新用户注册奖励");
    }

    /**
     * 获取可用余额 韦德 2018年8月21日15:50:04
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Double getWithdrawAmount(Integer userId) {
        return payMapper.selectWithdrawAmount(userId);
    }

    /**
     * 分页加载财务会计账目数据列表 韦德 2018年8月27日00:20:54
     *
     * @param page
     * @param limit
     * @param condition
     * @param trade_type
     * @param filter_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    @Override
    public List<Accounts> getAccountsLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractLimitWhere(condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        List<Accounts> list = accountMapper.selectLimit(page, limit, trade_type, trade_date_begin, trade_date_end, where);
        return list;
    }
    /**
     * 提取分页条件
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    private String extractLimitWhere(String condition, Integer trade_type, Integer filter_type,  String trade_date_begin, String trade_date_end) {
        // 查询模糊条件
        String where = " 1=1";
        if(filter_type == null  || filter_type == 0){
            if(condition != null) {
                condition = condition.trim();
                where += " AND (" + ConditionUtil.like("accounts_id", condition, true, "t1");
                where += " OR " + ConditionUtil.like("pay_id", condition, true, "t1");
                where += " OR " + ConditionUtil.like("contact_way", condition, true, "t2");
                if (condition.split("-").length == 2){
                    where += " OR " + ConditionUtil.like("add_date", condition, true, "t1");
                }
                where += " OR " + ConditionUtil.like("accounts_type", condition, true, "t1");
                where += " OR " + ConditionUtil.like("remark", condition, true, "t1") + ")";
            }

            // 查询全部数据或者只有一类数据
            where = extractQueryAllOrOne(trade_type, where);

            // 取两个日期之间或查询指定日期
            where = extractBetweenTime(trade_date_begin, trade_date_end, where);
        }else{
            if(condition != null) {
                condition = condition.trim();
                where += " AND ( 1=1 ";
                if(condition.contains("-")){
                    where += "AND  " + ConditionUtil.match("accounts_type", "2", true, "t1");
                    where += " AND " + ConditionUtil.match("amount", condition.replace("-","").trim(), true, "t1") + ")";
                }else if(condition.contains("+")){
                    where += "AND  " + ConditionUtil.match("accounts_type", "1", true, "t1");
                    where += " AND " + ConditionUtil.match("amount", condition.replace("+","").trim(), true, "t1") + ")";
                }else{
                    where += ")";
                }
            }
        }
        return where.trim();
    }


    /**
     * 提取两个日期之间的条件
     * @param trade_date_begin
     * @param trade_date_end
     * @param where
     * @return
     */
    protected String extractBetweenTime(String trade_date_begin, String trade_date_end, String where) {
        if ((trade_date_begin != null && trade_date_begin.contains("-")) &&
                trade_date_end != null && trade_date_end.contains("-")){
            where += " AND t1.add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (trade_date_begin != null && trade_date_begin.contains("-")){
            where += " AND t1.add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (trade_date_end != null && trade_date_end.contains("-")){
            where += " AND t1.add_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }
    /**
     * 查询全部数据或者只有一类数据
     * @param accountsType
     * @param where
     * @return
     */
    private String extractQueryAllOrOne(Integer accountsType, String where) {
        if (accountsType != null && accountsType != 0){
            where += " AND t1.accounts_type = #{accounts_type}";
        }
        return where;
    }
    /**
     * 统计分页加载财务会计账目数据列表
     *
     * @param condition
     * @param trade_type
     * @param filter_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    @Override
    public int getAccountsLimitCount(String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end) {
        String where = extractLimitWhere(condition, trade_type, filter_type,  trade_date_begin, trade_date_end);
        return accountMapper.selectLimitCount(trade_type, trade_date_begin, trade_date_end, where);
    }

    /**
     * 加载财务会计账目数据总条数 韦德 2018年8月27日00:21:16
     *
     * @return
     */
    @Override
    public int getAccountsCount() {
        return accountMapper.selectCount(new Accounts());
    }

    /**
     * 统计系统账户余额以及交易额 韦德 2018年8月27日11:21:35
     *
     * @param systemAccountsId
     * @return
     */
    @Override
    public UserResp getAccountAmount(Integer systemAccountsId) {
        UserResp userResp = this.getAccountAmountForCache(systemAccountsId);
        if(userResp == null) {
            userResp = this.getAccountAmountForDB(systemAccountsId);
        }
        return userResp;
    }

    /**
     * 取账户的总收入与总支出情况的实时数据 韦德 2018年8月7日00:43:31
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public UserResp getAccountAmountForDB(Integer id) {
        Map<String, BigDecimal> map = accountMapper.getSystemAccounts(id);
        if(!map.isEmpty()){
            Double incomeAmount = map.get("incomeAmount").doubleValue();
            Double expendAmount = map.get("expendAmount").doubleValue();
            if(incomeAmount != null && expendAmount != null){
                UserResp userResp = new UserResp();
                userResp.setIncomeAmount(incomeAmount);
                userResp.setExpendAmount(expendAmount);
                try {
                    redisTemplate.opsForValue().set("accountAmount_" + id, JsonUtil.getJson(map), 45, TimeUnit.MINUTES);
                }catch (Exception e){
                    System.err.println(e.toString());
                }
                return userResp;
            }
        }
        return null;
    }

    /**
     * 获取账户的总收入与总支出情况的缓存 韦德 2018年8月7日00:43:31
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public UserResp getAccountAmountForCache(Integer id) {
        Object obj = null;
        try{
            obj = redisTemplate.opsForValue().get("accountAmount_" + id);
        }catch (Exception e) {
            System.err.println(e.toString());
        }
        if(obj != null)
        {
            Map map = JSON.parseObject(String.valueOf(obj));
            UserResp userResp = new UserResp();
            userResp.setIncomeAmount(DecimalUtil.toDecimal(map.get("incomeAmount")).doubleValue());
            userResp.setExpendAmount(DecimalUtil.toDecimal(map.get("expendAmount")).doubleValue());
            return userResp;
        }
        return null;
    }

    /**
     * 分页加载交易流水 韦德 2018年8月27日21:55:17
     *
     * @param page
     * @param limit
     * @param condition
     * @param trade_type
     * @param filter_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    @Override
    public List<Pays> getPaysLimit(Integer page, String limit, String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractPaysLimitWhere(condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        List<Pays> list = payMapper.selectLimit(page, limit, trade_type, trade_date_begin, trade_date_end, where);
        return list;
    }
    /**
     * 提取交易流水分页条件
     * @param condition
     * @param trade_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    private String extractPaysLimitWhere(String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end) {
        // 查询模糊条件
        String where = " 1=1";
        if(filter_type == null || filter_type == 0){
            if(condition != null) {
                condition = condition.trim();
                where += " AND (" + ConditionUtil.like("pay_id", condition, true, "t1");
                where += " OR " + ConditionUtil.like("system_record_id", condition, true, "t1");
                where += " OR " + ConditionUtil.like("from_name", condition, true, "t1");
                where += " OR " + ConditionUtil.like("to_name", condition, true, "t1");
                if (condition.split("-").length == 2){
                    where += " OR " + ConditionUtil.like("add_time", condition, true, "t1");
                }
                where += " OR " + ConditionUtil.like("trade_type", condition, true, "t1");
                where += " OR " + ConditionUtil.like("remark", condition, true, "t1") + ")";
            }

            // 查询全部数据或者只有一类数据
            if (trade_type != null && trade_type != 0){
                where += " AND t1.trade_type = #{trade_type}";
            }
            // 取两个日期之间或查询指定日期
            where = extractBetweenTime(trade_date_begin, trade_date_end, where);
        }else{
            if(condition != null) {
                condition = condition.trim();
                where += " AND (" + ConditionUtil.like("from_name", condition, true, "t1");
                where += " OR " + ConditionUtil.like("to_name", condition, true, "t1");
                where += " OR " + ConditionUtil.like("amount", condition, true, "t1") + ")";
            }
        }
        return where.trim();
    }

    /**
     * 加载交易流水数据总条数 韦德 2018年8月27日21:55:32
     *
     * @return
     */
    @Override
    public int getPaysCount() {
        return payMapper.selectCount(new Pays());
    }

    /**
     * 加载交易流水数据分页总条数 韦德 2018年8月27日22:37:10
     *
     * @param condition
     * @param trade_type
     * @param filter_type
     * @param trade_date_begin
     * @param trade_date_end
     * @return
     */
    @Override
    public Integer getPaysLimitCount(String condition, Integer trade_type, Integer filter_type, String trade_date_begin, String trade_date_end) {
        String where = extractPaysLimitWhere(condition, trade_type, filter_type, trade_date_begin, trade_date_end);
        return payMapper.getPaysLimitCount(trade_type, trade_date_begin, trade_date_end, where);
    }

    /**
     * 充值 韦德 2018年8月7日03:05:53
     *
     * @param id
     * @param amount
     */
    @Override
    @Transactional
    public Boolean recharge(Integer id, Double amount) {
        PayParam payParam = new PayParam();
        payParam.setFromUid(Constant.SYSTEM_ACCOUNTS_ID);
        payParam.setAmount(amount);
        payParam.setToUid(id);
        payParam.setCurrency(0);
        payParam.setRemark("人工充值");
        this.recharge(payParam);
        return true;
    }

    /**
     * 更新交易回执单号 韦德 2018年8月30日14:54:32
     *
     * @param systemRecordId
     * @param channelRecordId
     * @return
     */
    @Override
    @Transactional
    public int updateChannelRecordId(Long systemRecordId, String channelRecordId) {
        return payMapper.updateChannelRecordId(systemRecordId, channelRecordId);
    }
    /**
     * 查询交易主体信息 韦德 2018年8月16日13:33:09
     * @param payParam
     * @return [0]=fromUser [1]=toUser
     */
    private List<UserDetailInfo> getUsersInfo(PayParam payParam){
        String userId = payParam.getFromUid().toString().concat(",").concat(payParam.getToUid().toString());
        List<UserDetailInfo> users = usersMapper.selectInUid(userId);
        System.out.println("交易参数:" + JsonUtil.getJson(payParam));
        if(users.isEmpty() || users.size() != 2) {
            throw new InfoException("查询交易主体账户信息不完整");
        }
        return users;
    }
    /**
     * 生成消费类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays extractConsumePayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        Pays pays = new Pays();
        pays.setPayId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(1).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(0).getUserName());

        pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getDictionaryId());
        pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getValue());

        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.consume").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.consume").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());

        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());

        pays.setSystemRecordId(payParam.getSystemRecordId());
        pays.setRemark(payParam.getRemark());

        pays.setStatus(0);
        return pays;
    }
    /**
     * 生成转账类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays extractTransferPayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        Pays pays = new Pays();
        pays.setPayId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(0).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(1).getUserName());

        pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getDictionaryId());
        pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getValue());

        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.deduction").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.deduction").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());

        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());

        pays.setSystemRecordId(payParam.getSystemRecordId());
        pays.setRemark(payParam.getRemark());

        pays.setStatus(0);
        return pays;
    }
    /**
     * 生成提现类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays extractWithdrawPayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        long payId = IdWorker.getFlowIdWorkerInstance().nextId();

        Pays pays = new Pays();
        pays.setPayId(payId);
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(1).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(0).getUserName());

        pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getDictionaryId());
        pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getValue());

        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.withdraw").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.withdraw").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());
        pays.setRemark(payParam.getRemark());
        pays.setStatus(0);

        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setSystemRecordId(payParam.getSystemRecordId());
        return pays;
    }

    /**
     * 生成退款类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays extractRefundPayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam)throws Exception {
        long payId = IdWorker.getFlowIdWorkerInstance().nextId();

        Pays pays = new Pays();
        pays.setPayId(payId);
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(0).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(1).getUserName());

        pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getDictionaryId());
        pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getValue());

        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.refund").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.refund").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());
        pays.setRemark(payParam.getRemark());
        pays.setStatus(1);

        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setSystemRecordId(payParam.getSystemRecordId());
        pays.setRemark(payParam.getRemark());
        return pays;
    }


    /**
     * 生成充值类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays extractRechargePayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        long payId = IdWorker.getFlowIdWorkerInstance().nextId();
        Pays pays = new Pays();
        pays.setPayId(payId);
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(0).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(1).getUserName());


        if(pays.getFromUid().equals(Constant.SYSTEM_ACCOUNTS_ID)){
            pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getDictionaryId());
            pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.internal").getValue());
        }else{
            pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getDictionaryId());
            pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getValue());
        }


        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.recharge").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.recharge").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());
        pays.setRemark(payParam.getRemark());
        pays.setStatus(0);
        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setSystemRecordId(payParam.getSystemRecordId());
        return pays;
    }
    /**
     * 生成往来账model
     * @param payParam
     * @param user
     * @param accountsType
     * @return
     * @throws Exception
     */
    private Accounts extractAccountModel(PayParam payParam, UserDetailInfo user, Integer accountsType) throws Exception {
        Accounts accounts = new Accounts();
        accounts.setAccountsId(IdWorker.getFlowIdWorkerInstance().nextId());
        accounts.setPayId(payParam.getSystemRecordId());
        accounts.setTradeAccountId(payParam.getFromUid());
        accounts.setTradeAccountName(user.getUserName());
        accounts.setAccountsType(accountsType);
        accounts.setAmount(payParam.getAmount());
        accounts.setRemark(payParam.getRemark());
        accounts.setCurrency(payParam.getCurrency());
        return accounts;
    }
    /**
     * 设置往来账
     * @param payParam
     * @param fromUserInfo
     * @param toUserInfo
     * @param accountsList
     * @param newPayParam
     */
    private void createAccounts(PayParam payParam, UserDetailInfo fromUserInfo, UserDetailInfo toUserInfo, List<Accounts> accountsList, PayParam newPayParam) {
        try {
            accountsList.add(extractAccountModel(payParam, fromUserInfo, 2));
            newPayParam.setFromUid(payParam.getToUid());
            accountsList.add(extractAccountModel(payParam, toUserInfo, 1));
        } catch (Exception e) {
            logger.error("生成往来账异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成往来账异常");
        }
    }

    /**
     * 获取数据 韦德 2018年8月13日13:26:57
     *
     * @param param
     * @return
     */
    @Override
    public Pays get(Pays param) {
        return null;
    }

    /**
     * 获取全部数据 韦德 2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<Pays> getList() {
        return null;
    }

    /**
     * 分页获取全部数据 韦德 2018年8月13日13:27:17
     *
     * @param page
     * @param limit
     * @param condition
     * @return
     */
    @Override
    public List<Pays> getLimit(Integer page, String limit, String condition) {
        return null;
    }

    /**
     * 插入数据 韦德 2018年8月13日13:27:48
     *
     * @param param
     * @return
     */
    @Override
    public int insert(Pays param) {
        return 0;
    }

    /**
     * 更新数据 韦德 2018年8月13日13:28:01
     *
     * @param param
     * @return
     */
    @Override
    public int update(Pays param) {
        return 0;
    }

    /**
     * 删除数据 韦德 2018年8月13日13:28:16
     *
     * @param param
     * @return
     */
    @Override
    public int delete(Pays param) {
        return 0;
    }


    /**
     * 分页加载用户流水信息  Timor  2019-2-27 10:57:59
     * @param page
     * @param limit
     * @param condition
     * @param userId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<Pays> selectPaysByPages(Integer page, String limit, String condition, Integer userId, String beginTime, String endTime) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractPaysLimitWhere(condition,beginTime, endTime);
        List<Pays> list = payMapper.selectPaysPage(limit,page,beginTime, endTime, userId,where);
        for (Pays pays:list){
            pays.setStrPayId(pays.getPayId().toString());
        }
        return list;
    }

    /**
     * 提取两个日期之间的条件
     * @return
     */
    private String extractPaysBetweenTime(String beginTime, String endTime, String where) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (beginTime != null && beginTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (endTime != null && endTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }

    /**
     * 提取分页(用户)条件
     * @return
     */
    private String extractPaysLimitWhere(String condition,String beginTime, String endTime) {
        // 查询模糊条件
        String where = " 1=1";
        if(condition != null&&condition.length()>0) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("pay_id", condition);
            if (condition.split("-").length == 2){
                where += " OR " + ConditionUtil.like("add_time", condition);

            }
           where+=")";
        }
        // 取两个日期之间或查询指定日期
        where = extractPaysBetweenTime(beginTime, endTime, where);
        return where.trim();
    }



    /**
     * 分页加载用户流水记录数  Timor   2019-2-27 10:58:32
     * @param condition
     * @param userId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer getPageCount(String condition, Integer userId, String beginTime, String endTime) {
        String where = extractPaysLimitWhere(condition,beginTime, endTime);
        return payMapper.selectPaysCount(where,beginTime, endTime, userId);
    }

    /**
     * 查询用户相应类型下的消费总额  Timor  2019-2-27 16:36:36
     * @param request
     * @param tradeType
     * @return
     */
    @Override
    public Double queryConsumeByType(HttpServletRequest request,Integer tradeType) {
        Integer userId=SessionUtil.getSession(request).getUserId();
        List<Pays> paysList=payMapper.selectPaysByType(tradeType,userId);
        Double total=0.00;
        for (Pays pays:paysList){
            total+=pays.getAmount();
        }
        return total;
    }

    /**
     * 本项目的充值  钉子 2019年2月27日16:48:01
     * @param userId
     * @param money
     * @return
     */
    @Override
    public Integer giveMoney(Integer userId, Double money) {
        PayParam payParam=new PayParam();
        payParam.setFromUid(1);
        payParam.setToUid(userId);
        payParam.setAmount(money);
        payParam.setRemark("支付宝充值");
        try {
            aliRecharge(payParam);
            return 1;
        }catch (Exception e){
            System.out.println(e.toString());
            return 0;
        }
    }

    /**
     * 本项目的提现 钉子 2019年2月27日16:50:59
     * @param userId
     * @param money
     * @return
     */
    @Override
    public Integer withMoney(Integer userId, Double money) {
        PayParam payParam=new PayParam();
        payParam.setFromUid(userId);
        payParam.setToUid(1);
        payParam.setAmount(money);
        try {
            withdraw(payParam);
            return 1;
        }catch (Exception e){
            System.out.println(e.toString());
            return 0;
        }
    }

    /**
     * 本项目的消费 钉子 2019年2月27日16:47:46
     * @param userId
     * @param money
     * @return
     */
    @Override
    @Transactional
    public Pays getMoney(Integer userId, Double money,Integer status) {
        if (status==0) {
            PayParam payParam = new PayParam();
            payParam.setFromUid(userId);
            payParam.setToUid(1);
            payParam.setAmount(money);
            payParam.setRemark("钱包消费");
            Pays pays=consumeReturn(payParam);
            return pays;
        }
        else if (status==1) {
            PayParam payParam = new PayParam();
            payParam.setFromUid(userId);
            payParam.setToUid(1);
            payParam.setAmount(money);
            payParam.setRemark("支付宝消费");
            Pays pays=aliConsume(payParam);
             return pays;
        }else{
            return null;
        }
    }

    /**
     * 消费 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    public Pays aliConsume(PayParam payParam) {
// 查询交易主体信息
        List<UserDetailInfo> userDetailInfoList = this.getUsersInfo(payParam);
        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();
        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = aliExtractConsumePayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) {
            throw new InfoException("生成交易流水失败");
        }

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) {
            throw new InfoException("乙方账户加款失败");
        }


        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) {
            throw new InfoException("生成往来账失败");
        }

        System.out.println("交易成功");
        return pays;
    }
    /**
     * 生成消费类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays aliExtractConsumePayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        Pays pays = new Pays();
        pays.setPayId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(0).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(1).getUserName());

        pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getDictionaryId());
        pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getValue());

        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.goods").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.goods").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.consume").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.consume").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());

        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());

        pays.setSystemRecordId(payParam.getSystemRecordId());
        pays.setRemark(payParam.getRemark());

        pays.setStatus(0);
        return pays;
    }
    /**
     * 充值 韦德 2018年8月16日13:17:17
     *
     * @param payParam
     */
    @Override
    @Transactional
    public Long aliRecharge(PayParam payParam) {
        logger.info("转账参数:" + JsonUtil.getJson(payParam));

        // 查询交易主体信息
        List<UserDetailInfo> userDetailInfoList = this.getUsersInfo(payParam);
        UserDetailInfo fromUserInfo = userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getFromUid())).findFirst().get();
        UserDetailInfo toUserInfo =userDetailInfoList.stream().filter(u -> u.getUserId().equals(payParam.getToUid())).findFirst().get();

        if(fromUserInfo.getBalance() <= 0) throw new InfoException("甲方钱包余额不足");

        // 生成交易流水账单
        Pays pays = null;
        try {
            pays = extractRechargePayModel(userDetailInfoList, payParam);
        } catch (Exception e) {
            logger.error("生成交易流水异常：" + JsonUtil.getJson(e));
            throw new InfoException("生成交易流水异常");
        }
        int count = payMapper.insert(pays);
        if(count == 0) throw new InfoException("生成交易流水失败");


        // 变更钱包余额
        count = 0;
        count = walletMapper.reduceBalance(fromUserInfo.getUserId(), payParam.getAmount(),fromUserInfo.getVersion());
        if(count == 0) throw new InfoException("甲方账户扣款失败");

        count = 0;
        count = walletMapper.addBalance(toUserInfo.getUserId(), payParam.getAmount(), toUserInfo.getVersion());
        if(count == 0) throw new InfoException("乙方账户加款失败");


        // 生成往来账目单
        List<Accounts> accountsList = new ArrayList<>();
        PayParam newPayParam = payParam;
        createAccounts(payParam, fromUserInfo, toUserInfo, accountsList, newPayParam);
        count = 0;
        count = accountMapper.insertList(accountsList);
        if(count == 0) throw new InfoException("生成往来账失败");

        System.out.println("交易成功");
        return payParam.getSystemRecordId();
    }

    /**
     * 生成充值类型model
     * @param userDetailInfoList
     * @param payParam
     * @return
     * @throws Exception
     */
    private Pays aliExtractRechargePayModel(List<UserDetailInfo> userDetailInfoList, PayParam payParam) throws Exception {
        long payId = IdWorker.getFlowIdWorkerInstance().nextId();
        Pays pays = new Pays();
        pays.setPayId(payId);
        pays.setFromUid(payParam.getFromUid());
        pays.setFromName(userDetailInfoList.get(0).getUserName());
        pays.setToUid(payParam.getToUid());
        pays.setToName(userDetailInfoList.get(1).getUserName());



         pays.setChannelType(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getDictionaryId());
         pays.setChannelName(DataDictionary.DATA_DICTIONARY.get("finance.pays.channel.alipay").getValue());


        pays.setProductType(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getDictionaryId());
        pays.setProductName(DataDictionary.DATA_DICTIONARY.get("finance.pays.product.currency").getValue());

        pays.setTradeType(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.recharge").getDictionaryId());
        pays.setTradeName(DataDictionary.DATA_DICTIONARY.get("finance.pays.trade.recharge").getValue());

        pays.setAddTime(new Date());
        pays.setAmount(payParam.getAmount());
        pays.setRemark(payParam.getRemark());
        pays.setStatus(0);
        payParam.setSystemRecordId(IdWorker.getFlowIdWorkerInstance().nextId());
        pays.setSystemRecordId(payParam.getSystemRecordId());
        return pays;
    }
}
