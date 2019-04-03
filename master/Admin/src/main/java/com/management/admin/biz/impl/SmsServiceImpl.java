/***
 * @pName Admin
 * @name SmsServiceImpl
 * @user HongWei
 * @date 2018/11/27
 * @desc
 */
package com.management.admin.biz.impl;

import com.aliyuncs.exceptions.ClientException;
import com.management.admin.biz.ISmsService;
import com.management.admin.utils.IdWorker;
import com.management.admin.utils.http.AliSmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements ISmsService {
    private final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private final RedisTemplate redisTemplate;

    @Autowired
    public SmsServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送手机用户注册验证码 DF 2018年11月27日00:47:18
     *
     * @param phone 手机号码 15000000000, 15000000001... 1000+
     * @return
     */
    @Override
    public long sendRegCode(String phone) {
        long randomCode = 0;
        boolean sendResult = false;

        try {
            // 生成纯数字随机验证码
            randomCode = IdWorker.getFlowIdWorkerInstance().nextId();
            randomCode = Long.parseLong((randomCode + "").substring(0, 6));

            // 调用短信服务中心远程接口发送验证码到客户端手机
//            sendResult = AliSmsUtil.sendMessage(phone, "易好家", "SMS_156895520", String.format("{\"code\":\"%s\"}", randomCode), phone);
            System.out.println("验证码为："+randomCode);
            // 存储正确结果到缓存服务器中, 有效期5分钟
            if(!sendResult){
                redisTemplate.opsForValue().set("sms-" + phone, randomCode + "", 5, TimeUnit.MINUTES);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!sendResult)
            logger.error("SmsServiceImpl_sendRegCode()_注册短信发送失败_" + phone);

        return randomCode;
    }

    /**
     * 发送找回密码短信验证码 DF 2018年12月7日02:06:02
     *
     * @param phone 手机号码 15000000000 必填
     * @return
     */
    @Override
    public long sendResetPwdSmsCode(String phone) {
        long randomCode = 0;
        boolean sendResult = false;

        try {
            // 生成纯数字随机验证码
            randomCode = IdWorker.getFlowIdWorkerInstance().nextId();
            randomCode = Long.parseLong((randomCode + "").substring(0, 6));

            // 调用短信服务中心远程接口发送验证码到客户端手机
//            sendResult = AliSmsUtil.sendMessage(phone, "易好家", "SMS_156895526", String.format("{\"code\":\"%s\"}", randomCode), phone);

            // 存储正确结果到缓存服务器中, 有效期5分钟
            if(!sendResult){
                redisTemplate.opsForValue().set("sms-" + phone, randomCode + "", 5, TimeUnit.MINUTES);
                Object o = redisTemplate.opsForValue().get("sms-" + phone);
                System.out.println("刚刚放入的验证码为："+o.toString());
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!sendResult)
            logger.error("SmsServiceImpl_sendResetPwdSmsCode()_充值密码发送失败_" + phone);

        return randomCode;
    }

    /**
     * 发送手机商家申请入驻验证码 DF 2018年11月27日00:47:18
     *
     * @param phone 手机号码 15000000000, 15000000001... 1000+
     * @return
     */
    @Override
    public boolean sendApplyCode(String phone) {

        long randomCode = 0;
        boolean sendResult = false;

        try {
            // 生成纯数字随机验证码
            randomCode = IdWorker.getFlowIdWorkerInstance().nextId();
            randomCode = Long.parseLong((randomCode + "").substring(0, 6));

            // 调用短信服务中心远程接口发送验证码到客户端手机
            sendResult = AliSmsUtil.sendMessage(phone, "易好家", "SMS_156900489", String.format("{\"code\":\"%s\"}", randomCode), phone);

            // 存储正确结果到缓存服务器中, 有效期5分钟
            if(sendResult){
                redisTemplate.opsForValue().set("sms-" + phone, randomCode + "", 5, TimeUnit.MINUTES);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!sendResult)
            logger.error("SmsServiceImpl_sendRegCode()_注册短信发送失败_" + phone);

        return sendResult;
    }
    /**
     * 发送手机商家申请入驻验证码 DF 2018年11月27日00:47:18
     *
     * @param phone 手机号码 15000000000, 15000000001... 1000+
     * @return
     */
    @Override
    public boolean sendAccountCode(String phone) {

        long randomCode = 0;
        boolean sendResult = false;

        try {
            // 生成纯数字随机验证码
            randomCode = IdWorker.getFlowIdWorkerInstance().nextId();
            randomCode = Long.parseLong((randomCode + "").substring(0, 6));

            // 调用短信服务中心远程接口发送验证码到客户端手机
            sendResult = AliSmsUtil.sendMessage(phone, "易好家", "SMS_156895534", String.format("{\"code\":\"%s\"}", randomCode), phone);

            // 存储正确结果到缓存服务器中, 有效期5分钟
            if(sendResult){
                redisTemplate.opsForValue().set("sms-" + phone, randomCode + "", 5, TimeUnit.MINUTES);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!sendResult)
            logger.error("SmsServiceImpl_sendRegCode()_注册短信发送失败_" + phone);

        return sendResult;
    }
}
