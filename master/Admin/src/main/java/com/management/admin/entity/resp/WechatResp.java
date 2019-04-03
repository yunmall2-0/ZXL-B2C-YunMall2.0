package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信登陆统一响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatResp {

    private String access_token;

    private String expires_in;

    private String refresh_token;

    private String openid;

    private String scope;

    private String nickname;

    private String sex;

    private String province;

    private String city;

    private String country;

    private String headimgurl;

    private String privilege;

    private String unionid;

    private String errcode;

    private String errmsg;
}
