package com.management.admin.apiController.wehat.lly835.config;

import com.lly835.bestpay.config.WxPayH5Config;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName WeChatPayConfig
 * @Description 微信公众账号支付配置
 * @Author Timor
 * @Date 2018/12/22 10:20
 * Version 1.0
 **/

@Component
public class PayConfig {

    @Autowired
    private WechatAccountConfig accountConfig;

    @Bean
    public WxPayH5Config wxPayH5Config() {
        WxPayH5Config wxPayH5Config = new WxPayH5Config();
        wxPayH5Config.setAppId(accountConfig.getMpAppId()); //设置微信公众号的appid
        wxPayH5Config.setMchId(accountConfig.getMchId());   // // 设置商户号
        wxPayH5Config.setMchKey(accountConfig.getMchKey()); // 设置商户密钥
        wxPayH5Config.setKeyPath(accountConfig.getKeyPath());  // 设置商户证书路径
        wxPayH5Config.setNotifyUrl(accountConfig.getNotifyUrl()); // 设置支付后异步通知url
        return wxPayH5Config;
    }

    //把你要实例化的对象转化成一个Bean，放在IoC容器中
    @Bean
    public BestPayServiceImpl bestPayService(WxPayH5Config wxPayH5Config) {

        //支付类, 所有方法都在这个类里
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayH5Config(wxPayH5Config);
        return bestPayService;
    }
}
