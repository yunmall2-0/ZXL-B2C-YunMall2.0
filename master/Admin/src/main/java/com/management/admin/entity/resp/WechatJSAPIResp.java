package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信jsapi统一相应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatJSAPIResp {

    private String access_token;

    private String expires_in;

    private String errcode;

    private String errmsg;

    private String ticket;


}
