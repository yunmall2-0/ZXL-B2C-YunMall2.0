package com.management.admin.apiController;

import com.google.common.collect.ImmutableMap;
import com.management.admin.biz.IUserService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.resp.WechatResp;
import com.management.admin.utils.HttpUtils;
import com.management.admin.utils.JsonUtil;
import com.management.admin.utils.MD5Util;
import com.management.admin.utils.TokenUtil;
import com.management.admin.utils.web.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("api/wxlogin")
public class WechatLoginApiController {
    @Autowired
    private IUserService userService;

    /**
     * 发起登录请求
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "WXLogin")
    public String weixinLogin(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        String takenUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&" +
                "redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        takenUrl = takenUrl.replace("APPID", Constant.APP_ID);//
        takenUrl = takenUrl.replace("REDIRECT_URI", URLEncoder
                .encode(Constant.CALLBACK + "/api/wxlogin/receiveWXAuthRedirect", "UTF-8"));//替换回调地址，并用URLEncoder编码
        takenUrl = takenUrl.replace("SCOPE", "snsapi_base");

        return "redirect:" + takenUrl;
    }

    /**
     * 微信登录回调
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "receiveWXAuthRedirect", method = RequestMethod.GET)
    public String receiveWXAuthRedirect(String code, HttpServletRequest request,
                                        HttpServletResponse response, final Model model) throws Exception {

        // 获取token 和openid
        String access_token_Uri = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        access_token_Uri = access_token_Uri.replace("APPID", Constant.APP_ID);
        access_token_Uri = access_token_Uri.replace("SECRET", Constant.APP_SECRET);
        access_token_Uri = access_token_Uri.replace("CODE", code);
        // 如果缓存中没有token就发起请求去请求token
        // 定义请求结果
        WechatResp accessTokenResult = (WechatResp) request.getSession().getAttribute("accessTokenResult");
        if (accessTokenResult == null) {
            // 发起请求
            String result = HttpUtils.post(access_token_Uri);
            // 转换相应结果
            accessTokenResult = JsonUtil.getModel(result, WechatResp.class);
        }
        // 如果请求成功
        if (accessTokenResult != null && accessTokenResult.getAccess_token() != null && accessTokenResult.getOpenid() != null) {
            // session域缓存token防止微信多次回调
            request.getSession().setAttribute("accessTokenResult", accessTokenResult);


            // 初始化请求信息（用户没有注册过）
            String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessTokenResult.getAccess_token() + "&openid=" + accessTokenResult.getOpenid() + "&lang=zh_CN";

            // 发起请求
            String s = HttpUtils.get(url);
            // 转换请求结果，获取用户信息
            WechatResp userInfo = JsonUtil.getModel(s, WechatResp.class);
            // 去查询这个用户有没有注册过
            Users user = userService.selectUserByUnionid(userInfo.getUnionid());
            if (user == null) {


                // 用户注册
                Users users = userService.registerWechat(userInfo);
                // 本地缓存
                setCookie(users, request, response);
                // TODO 跳转微信首页
                return "redirect:http://baidu.com";
            } else {
                // 如果这个用户被禁用
                if (user.getState().equals(1)) {
                    // TODO 跳转错误页面
                    return "redirect:http://baidu.com";
                } else {
                    // 本地缓存
                    setCookie(user, request, response);
                    // TODO 跳转微信首页
                    return "redirect:http://baidu.com";
                }
            }
        }
        return "redirect:http://baidu.com";
    }

    /**
     * 本地缓存 狗蛋 2019年2月23日15:54:26
     *
     * @param user
     * @param request
     * @param response
     */
    private void setCookie(Users user, HttpServletRequest request, HttpServletResponse response) {
        // 生成平台令牌
        Map<String, String> fields
                = ImmutableMap.of("userName", user.getUserName(), "userId", user.getUserId() + "");
        String token = TokenUtil.create(fields);

        // 本地缓存
        CookieUtil.setCookie(request, response, "token", token);
        // 续期
        String key = "token:" + MD5Util.encrypt16(user.getUserName() + user.getUserId());
        /*redisTemplate.opsForValue().set(key, token, 30, TimeUnit.DAYS);*/
    }

}
