package com.management.admin.apiController;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.lly835.bestpay.rest.type.Post;
import com.management.admin.biz.ISmsService;
import com.management.admin.biz.IUserService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.db.Wallets;
import com.management.admin.entity.resp.WechatResp;
import com.management.admin.exception.InfoException;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.*;
import com.management.admin.utils.web.CookieUtil;
import com.management.admin.utils.web.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/user")
public class UserApiController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ISmsService smsService;



    /**
     * 用户注册 狗蛋 2019年2月23日09:40:20
     *
     * @param      usersStr
     * @return
     */
    @PostMapping("register")
    public JsonResult register(String usersStr) {
        if (usersStr == null) throw new MsgException("用户信息不能为空！");
        Users model = JsonUtil.getModel(usersStr, Users.class);
        Users result = userService.register(model);
        if (result == null) throw new MsgException("注册失败！");
        result.setPassword("");
        if (result.getContactWay().length() == 11) {
            String encrypt = PhoneUtil.getEncrypt(result.getContactWay());
            result.setContactWay(encrypt);
        }

        return new JsonResult().successful(result);
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("sendRegMsg")
    public JsonResult sendRegMsg(String phone){
        long l = smsService.sendRegCode(phone);
        if(l==0) throw new MsgException("验证码发送失败");
        return new JsonResult(200,l);
    }

    /**
     * 用户注册（app）
     * @param phone
     * @param password
     * @param code
     * @return
     */
    @PostMapping("registerApp")
    public JsonResult registerApp(String phone,String password,String code){
        Users users = userService.registerApp(phone, password, code);
        if(users == null) throw new MsgException("注册失败");
        return JsonResult.successful();
    }

    /**
     * 发送找回验证码短信
     * @param phone
     * @return
     */
    @PostMapping("sendFindPassword")
    public JsonResult sendFindPassword(String phone){
        long l = smsService.sendResetPwdSmsCode(phone);
        if(l==0) throw new MsgException("验证码发送失败");
        return new JsonResult(200,l);
    }

    @PostMapping("findPassword")
    public JsonResult findPassword(String phone,String password,String code){
        Integer result = userService.findPassword(phone,password,code);

        if(result<=0) throw new MsgException("重置密码失败！");
        return JsonResult.successful();
    }

    /**
     * 用户登录（前台） 狗蛋 2019年2月23日13:52:06
     *
     * @param userName
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public JsonResult login(String userName, String password, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) throw new MsgException("用户名或者账号不能为空！");
        Users user = userService.login(userName, password);

        // 生成平台令牌
        Map<String, String> fields
                = ImmutableMap.of("userName", user.getUserName(), "userId", user.getUserId() + "");
        String token = TokenUtil.create(fields);
        // 续期
        String key = "token:" + MD5Util.encrypt16(user.getUserName() + user.getUserId());
        request.getSession().setAttribute("token",token);
//        redisTemplate.opsForValue().set(key, token, 30, TimeUnit.DAYS);
/*        // 隐藏用户隐秘信息
        user.setPassword("");
        String contactWay = user.getContactWay();
        if(user.getContactWay()!=null&&contactWay.length()==11){
            user.setContactWay(PhoneUtil.getEncrypt(contactWay));
        }*/
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token
        return new JsonResult(200,user.getUserId());
    }

    /**
     * 用户注销 狗蛋 2019年2月23日11:02:12
     *
     * @return
     */
    @GetMapping("logout")
    @ResponseBody
    public JsonResult logout(HttpServletRequest request, HttpServletResponse response) {
        String token = SessionUtil.getToken(request);
//        userService.logout(token);
        CookieUtil.deleteCookie(request, response, "token");
        return JsonResult.successful();
    }

    /**
     * 查询用户支付密码是否存在  Timor  2019-2-25 14:57:12
     *
     * @param request
     * @return
     */
    @GetMapping(value = "queryPayPassword")
    @ResponseBody
    public JsonResult queryPayPassword(HttpServletRequest request) {
        Integer userId = SessionUtil.getSession(request).getUserId();
        String payPassword = userService.getUserById(userId).getSecurityPassword();
        if (payPassword == null || payPassword == "" || payPassword.length() == 0) {
            return JsonResult.failing();
        }
        return JsonResult.successful();
    }

    /**
     * 设置用户支付密码 Timor  2019-2-25 14:57:06
     *
     * @param request
     * @param oldPayPassword
     * @param newPayPassword
     * @return
     */
    @PostMapping(value = "changePaymentPassword")
    @ResponseBody
    public JsonResult changePayPassword(HttpServletRequest request, String oldPayPassword, String newPayPassword) {
        Integer result = userService.changePayPassword(request, oldPayPassword, newPayPassword);
        if (result == -2) {
            return new JsonArrayResult().failing("原始密码不正确");
        } else if (result > 0) {
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

    /**
     * 验证用户支付密码 狗蛋 2019年2月27日14:27:13
     *
     * @param payPassword
     * @param request
     * @param response
     * @return
     */
    @PostMapping("verifyPayPassword")
    public JsonResult verifyPayPassword(String payPassword, HttpServletRequest request, HttpServletResponse response) {
        Integer userId = SessionUtil.getSession(request).getUserId();
        Boolean aBoolean = userService.verifyPayPassword(userId, payPassword);
        if (aBoolean) return new JsonResult(200, "success");
        return JsonResult.failing();
    }


    /**
     * 修改用户登录密码 Timor  2019-2-25 14:56:57
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @PostMapping(value = "changePassword")
    @ResponseBody
    public JsonResult changePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        Integer result = userService.changePassword(request, oldPassword, newPassword);
        if (result > 0) {
            return JsonResult.successful();
        } else if (result == -2) {
            return new JsonArrayResult().failing("原始密码不正确");
        }
        return JsonResult.failing();
    }


    /**
     * 查询用户钱包信息 Timor  2019-2-26 17:20:08
     *
     * @param request
     * @return
     */
    @GetMapping(value = "queryWallet")
    public JsonResult queryWallet(HttpServletRequest request) {
        Integer userId = SessionUtil.getSession(request).getUserId();
        Wallets wallets = userService.queryWallets(userId);
        if (wallets != null) {
            return new JsonArrayResult().successful(wallets);
        }
        return JsonResult.failing();
    }

    /**
     * app 微信登录
     * @param wechatStr
     * @return
     */
    @PostMapping("wechatLogin")
    public JsonResult wechatLogin(String wechatStr){
        if(StringUtils.isBlank(wechatStr)) throw new MsgException("参数为空");
        WechatResp model = JsonUtil.getModel(wechatStr, WechatResp.class);
        Users users = userService.selectUserByUnionid(model.getUnionid());
        if(users == null){
            Users users1 = userService.registerWechat(model);
            // 生成平台令牌
            Map<String, String> fields
                    = ImmutableMap.of("userName", users1.getUserName(), "userId", users1.getUserId() + "");
            String token = TokenUtil.create(fields);
            return new JsonResult(200,token);
        }else{
            // 生成平台令牌
            Map<String, String> fields
                    = ImmutableMap.of("userName", users.getUserName(), "userId", users.getUserId() + "");
            String token = TokenUtil.create(fields);
            return new JsonResult(200,token);
        }
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @PostMapping("getUserInfo")
    public JsonResult getUserInfo(String userId){
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token


        Integer userids = 0; // 代表最后的用户编号值
        if(userId.length()>19){
            Map<String, String> validate = TokenUtil.validate(userId);
            userids = Integer.parseInt(validate.get("userId"));
        }else{
            userids = Integer.parseInt(userId);
        }
        Users user = userService.getUserById(userids);
        if(user == null) throw new MsgException("获取用户信息失败");
                // 隐藏用户隐秘信息
        user.setPassword("");
        user.setSecurityPassword("");
        String contactWay = user.getContactWay();
        if(user.getContactWay()!=null&&contactWay.length()==11){
            user.setContactWay(PhoneUtil.getEncrypt(contactWay));
        }
        return new JsonResult(200,user);
    }

}
