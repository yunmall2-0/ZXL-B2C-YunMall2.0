package com.management.admin.biz;

import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.db.Wallets;
import com.management.admin.entity.resp.UserInfo;
import com.management.admin.entity.resp.UserResp;
import com.management.admin.entity.resp.WechatResp;
import io.swagger.models.auth.In;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

public interface IUserService extends IBaseService<Users> {

    /**
     * 根据用户名查询用户 狗蛋 2019年2月22日14:47:25
     * @param userName
     * @return
     */
    Users staffLoginByUname(String userName);

    /**
     * 用户登录 狗蛋 2019年2月22日14:46:53
     * @param userName
     * @param password
     * @return
     */
    UserInfo staffLogin(String userName,String password);

    /**
     * 用户注销 狗蛋 2019年2月22日14:47:09
     * @param token
     */
    void logout(String token);

    /**
     * 根据用户名查询未禁用的用户 狗蛋 2019年2月22日10:39:54
     * @param userName
     * @return
     */
    Users selectUserByUserName(String userName);

    /**
     * 根据用户名查询用户 狗蛋 2019年2月22日14:46:45
     * @param userName
     * @return
     */
    Users selectByUserName(String userName);

    /**
     * 用户注册（官方网站）狗蛋 2019年2月22日14:46:40
     * @param users
     * @return
     */
    Users register(Users users);


    /**
     * 修改用户最后一次登陆时间 狗蛋 2019年2月22日14:22:21
     * @param userId
     * @param lastTime
     * @return
     */
    Integer updateUserLastTime(Integer userId, Date lastTime);


    /**
     * (分页)查询所有用户
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    List<Users> getUsersLimit(Integer page, String limit, String condition,String beginTime, String endTime);


    /**
     * 分页记录数 Timor 2019-2-20 11:30:57
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer getLimitCount(String condition,String beginTime, String endTime);

    /**
     * 修改用户状态 Timor 2019-2-22 18:06:38
     * @param state
     * @param userId
     * @return
     */
    Integer  updateDisable(Integer state,Integer userId);

    /**
     * 根据用户Id查找用户 Timor 2019-2-23 09:40:07
     * @param userId
     * @return
     */
    Users getUserById(Integer userId);

    /**
     * 用户登录（前台） 狗蛋 2019年2月23日09:43:25
     * @param userName
     * @param password
     * @return
     */
    Users login(String userName,String password);

    /**
     * 根据ID查找用户信息  钉子  2019年2月22日14:07:53
     * @param userId
     * @return
     */
    UserResp getUsersById(Integer userId);
    /**
     * 根据用户名查询用户信息 钉子 2019年2月22日14:13:35
     * @param username
     * @return
     */
    Users getUserByUserName(String username);

    /**
     * 无流水充值 钉子 2019年2月22日14:18:13
     * @param username
     * @param amount
     * @return
     */
    int changeBalance(String username, Double amount);


    /**
     * 修改用户信息  Timor  2019-2-23 11:38:17
     * @param users
     * @return
     */
    Integer updateUsersById(Users users);

    /**
     * 根据微信的openid 获取用户信息 狗蛋 2019年2月23日15:41:14
     * @param openId
     * @return
     */
    Users getUserByOpenId(String openId);

    /**
     * 用户注册（微信）
     * @param wechatResp
     * @return
     */
    Users registerWechat(WechatResp wechatResp);


    /**
     * 后台新增用户 Timor 2019-2-23 16:31:07
     * @param users
     * @return
     */
    Integer insertUsers(Users users) ;


    /**
     * 设置支付密码(存在：修改支付密码 不存在：设置原始初始密码)
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Integer changePayPassword(HttpServletRequest request,String oldPassword,String newPassword);

    /**
     * 修改用户登录支付密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Integer changePassword(HttpServletRequest request,String oldPassword,String newPassword);

    /**
     * 查询用户钱包信息 Timor  2019-2-26 17:13:18
     * @param userId
     * @return
     */
    Wallets queryWallets(Integer userId);

    /**
     * 验证用户支付密码 狗蛋 2019年2月27日14:21:48
     * @param userId
     * @param payPassword
     * @return
     */
    Boolean verifyPayPassword(Integer userId,String payPassword);

    /**
     * 根据微信唯一标识获取用户
     * @param unionid
     * @return
     */
    Users selectUserByUnionid(String unionid);

    /**
     * app用户注册
     */
    Users registerApp(String phone,String password,String code);

    /**
     * 找回密码
     * @param phone
     * @param password
     * @return
     */
    Integer findPassword(String phone,String password,String code);
}
