package com.management.admin.apiController.wehat.lly835.config;

        import lombok.*;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.stereotype.Component;

/**
 * @ClassName WechatAccountConfig
 * @Description 微信公众号账号设置
 * @Author Timor
 * @Date 2018/12/22 10:15
 * Version 1.0
 **/
@Data
@ConfigurationProperties(prefix = "wechat")
@Component
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WechatAccountConfig {
    /**
     * 公众账号ID
     */
    private String mpAppId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String keyPath;

    /**
     * 微信支付异步通知地址
     */
    private String notifyUrl;
}
