package com.management.admin.biz.impl;

import com.management.admin.biz.IUserService;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.PermissionRelation;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.db.Wallets;
import com.management.admin.entity.dbExt.UserDetailInfo;
import com.management.admin.entity.resp.UserInfo;
import com.management.admin.entity.resp.WechatResp;
import com.management.admin.exception.InfoException;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.PermissionMapper;
import com.management.admin.repository.PermissionRelationMapper;
import com.management.admin.entity.resp.UserResp;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.UsersMapper;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.repository.WalletMapper;
import com.management.admin.utils.IdWorker;
import com.management.admin.utils.PropertyUtil;
import com.management.admin.utils.MD5Util;
import com.management.admin.utils.TokenUtil;
import com.management.admin.utils.web.SessionUtil;
import com.qcloud.cos.utils.Md5Utils;
import lombok.val;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends BaseServiceImpl<Users> implements IUserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final UsersMapper usersMapper;
    private final PermissionRelationMapper permissionRelationMapper;
    private WalletMapper walletMapper;
    private final PermissionMapper permissionMapper;

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, PermissionRelationMapper permissionRelationMapper, PermissionMapper permissionMapper, WalletMapper walletMapper) {
        this.usersMapper = usersMapper;
        this.permissionRelationMapper = permissionRelationMapper;
        this.permissionMapper = permissionMapper;
        this.walletMapper = walletMapper;
    }


    /**
     * 分页查询
     *
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<Users> getUsersLimit(Integer page, String limit, String condition, String beginTime, String endTime) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractLimitWhere(condition, beginTime, endTime);
        List<Users> list = usersMapper.selectLimit(page, limit, beginTime, endTime, where);
        return list;
    }


    /**
     * 加载分页记录数 韦德 2018年8月30日11:29:22
     *
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer getLimitCount(String condition, String beginTime, String endTime) {
        String where = extractLimitWhere(condition, beginTime, endTime);
        return usersMapper.selectLimitCount(beginTime, endTime, where);
    }


    /**
     * 提取分页条件
     *
     * @return
     */
    private String extractLimitWhere(String condition, String beginTime, String endTime) {
        // 查询模糊条件
        String where = " 1=1";
        if (condition != null && condition.length() > 0) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("user_id", condition);
            if (condition.split("-").length == 2) {
                where += " OR " + ConditionUtil.like("add_time", condition);

            }
            where += " OR " + ConditionUtil.like("user_name", condition);
            if ("管理员".equals(condition)) {
                where += " OR " + ConditionUtil.like("role_id", "1");
            }
            if ("会员".equals(condition)) {
                where += " OR " + ConditionUtil.like("role_id", "4");
            }
            if ("游客".equals(condition)) {
                where += " OR " + ConditionUtil.like("role_id", "5");
            }
            where += " OR " + ConditionUtil.like("last_time", condition);
            if ("正常".equals(condition)) {
                where += " OR " + ConditionUtil.like("state", "0");
            }
            if ("禁用".equals(condition)) {
                where += " OR " + ConditionUtil.like("state", "1");
            }
            where += " OR " + ConditionUtil.like("contact_way", condition) + ")";
        }
        // 取两个日期之间或查询指定日期
        where = extractBetweenTime(beginTime, endTime, where);
        return where.trim();
    }


    /**
     * 提取两个日期之间的条件
     *
     * @return
     */
    protected String extractBetweenTime(String beginTime, String endTime, String where) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        } else if (beginTime != null && beginTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        } else if (endTime != null && endTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }

    /**
     * 获取数据  2018年8月13日13:26:57
     *
     * @param param
     * @return
     */
    @Override
    public Users get(Users param) {
        return super.get(param);
    }

    /**
     * 获取全部数据  2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<Users> getList() {
        return super.getList();
    }


    /**
     * 插入数据  2018年8月13日13:27:48
     *
     * @param param
     * @return
     */
    @Override
    public int insert(Users param) {
        return super.insert(param);
    }

    /**
     * 更新数据  2018年8月13日13:28:01
     *
     * @param param
     * @return
     */
    @Override
    public int update(Users param) {
        return super.update(param);
    }

    /**
     * 删除数据  2018年8月13日13:28:16
     *
     * @param param
     * @return
     */
    @Override
    public int delete(Users param) {
        return super.delete(param);
    }


    @Override
    public List<Users> getLimit(Integer page, String limit, String condition) {
        return super.getLimit(page, limit, condition);
    }


    @Override
    public Users staffLoginByUname(String userName) {
        return selectUserByUserName(userName);
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    @Override
    public UserInfo staffLogin(String userName, String password) {
        return null;
    }

    @Override
    public void logout(String token) {
        Map<String, String> map = TokenUtil.validate(token);
        if (!map.isEmpty()) {
            String key = "token:" + MD5Util.encrypt16(map.get("userName") + map.get("userId"));
            redisTemplate.delete(key);
        }
    }

    /**
     * 根据用户名查询用户 狗蛋 2019年2月22日10:39:54
     *
     * @param userName
     * @return
     */
    @Override
    public Users selectUserByUserName(String userName) {
        return usersMapper.selectUserByUserName(userName);
    }

    /**
     * 根据用户名查询用户 狗蛋 2019年2月22日14:21:58
     *
     * @param userName
     * @return
     */
    @Override
    public Users selectByUserName(String userName) {
        List<Users> usersList = usersMapper.selectByUserName(userName);
        Users result = new Users();
        for (Users user : usersList) {
            if (StringUtils.isEmpty(user.getOpenId())) {
                result = user;
            }
        }
        return result;
    }

    /**
     * 用户注册（官方网站） 狗蛋 2019年2月22日14:22:02
     *
     * @param users
     * @return
     */
    @Override
    @Transactional
    public Users register(Users users) {
        // 用户注册 1：验证用户名是否存在，2：加盐加密用户信息，3：设置用户权限，4：加入数据库

        List<Users> usersList = usersMapper.selectByUserName(users.getUserName());
        if (usersList.size() > 0) throw new MsgException("用户名已存在！");
        users.setRoleId(4);
        // 加盐加密用户信息
        String newPassword = MD5Util.encrypt32(MD5Util.encrypt32(users.getUserName() + users.getPassword()));
        users.setPassword(newPassword);
        String nickName ="";
        try {
            nickName = "用户" + IdWorker.getFlowIdWorkerInstance().nextId(6);
        } catch (Exception e) {
            e.printStackTrace();
        }
        users.setNickName(nickName);
        users.setPhoto("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3987166397,2421475227&fm=26&gp=0.jpg");
        // 注册数据库
        Integer integer = usersMapper.insertUsers(users);
        if (integer <= 0) {
            throw new MsgException("用户注册失败！");
        }
        // 初始化用户权限信息
        PermissionRelation permissionRelation = new PermissionRelation();
        permissionRelation.setUid(users.getUserId());
        permissionRelation.setPermissionList(4);
        // 注册数据库
        int insert = permissionRelationMapper.insert(permissionRelation);
        if (insert <= 0) {
            throw new MsgException("初始化用户权限失败！");
        }
        Wallets wallets = new Wallets();
        wallets.setUserId(users.getUserId());
        wallets.setBalance(0D);
        wallets.setUpdateTime(new Date());
        wallets.setVersion(0);
        int count = 0;
        count = walletMapper.insert(wallets);
        if (count == 0) {
            throw new MsgException("开通钱包失败");
        }
        return users;
    }

    /**
     * 修改用户最后一次登陆时间 2019年2月22日14:22:21
     *
     * @param userId
     * @param lastTime
     * @return
     */
    @Override
    public Integer updateUserLastTime(Integer userId, Date lastTime) {
        return usersMapper.updateUserLastTime(userId, lastTime);
    }


    /**
     * 修改用户状态 Timor 2019-2-22 18:06:38
     *
     * @param state
     * @param userId
     * @return
     */
    @Override
    public Integer updateDisable(Integer state, Integer userId) {
        return usersMapper.updateState(state, userId);
    }


    /**
     * 用户登录（前台） 狗蛋 2019年2月23日09:43:25
     *
     * @param userName
     * @param password
     * @return
     */
    @Override
    public Users login(String userName, String password) {
        // 加盐加密用户信息，去跟数据库对比
        String newPassword = MD5Util.encrypt32(MD5Util.encrypt32(userName + password));
        Users users = usersMapper.login(userName, newPassword);

        if (users == null) throw new MsgException("登录失败，账号或者密码错误！");

        if (users.getState().equals(1)) throw new MsgException("您的账号已被禁用，无法登录！");
        // 修改用户登陆时间
        usersMapper.updateUserLastTime(users.getUserId(), new Date());
        return users;
    }

    /**
     * 根据用户ID查找用户信息 钉子 2019年2月22日14:08:35
     *
     * @param userId
     * @return
     */
    @Override
    public UserResp getUsersById(Integer userId) {
        UserDetailInfo userInfo = usersMapper.selectUserDetail(userId.toString());
        if (userInfo == null) {
            return null;
        }
        UserResp userResp = new UserResp();
        PropertyUtil.clone(userInfo, userResp);
        return userResp;
    }

    /**
     * 根据用户名查询用户信息 钉子 2019年2月22日14:14:08
     *
     * @param username
     * @return
     */
    @Override
    public Users getUserByUserName(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        Users users = new Users();
        users.setUserName(username);
        criteria.andEqualTo("userName", users.getUserName());
        List<Users> usersList = usersMapper.selectByExample(example);
        if (users != null && usersList.size() > 0) {
            return usersList.get(0);
        }
        return null;
    }

    /**
     * 根据用户Id查找用户 Timor 2019-2-23 09:40:07
     *
     * @param userId
     * @return
     */
    @Override
    public Users getUserById(Integer userId) {
        return usersMapper.slectUserById(userId);
    }

    /**
     * 无流水充值 钉子 2019年2月22日14:18:57
     *
     * @param username
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public int changeBalance(String username, Double amount) {
        Users user = this.getUserByUserName(username);
        if (user == null) {
            throw new MsgException("用户不存在");
        }
        usersMapper.updateByPrimaryKey(user);
        Wallets wallets = walletMapper.selectByUid(user.getUserId());
        if (wallets == null) {
            throw new MsgException("查询钱包失败");
        }
        wallets.setBalance(wallets.getBalance() + amount);
        wallets.setUpdateTime(new Date());
        int count = walletMapper.updateByPrimaryKey(wallets);
        if (count == 0) {
            throw new MsgException("更新失败");
        }
        return count;
    }


    /**
     * 修改用户信息 Timor 2019-2-23 11:38:59
     *
     * @param users
     * @return
     */
    @Override
    @Transactional
    public Integer updateUsersById(Users users) {

        String pwd = users.getPassword();
        String payPwd = users.getSecurityPassword();
        //登录密码加密
        if (pwd != null && pwd.length() != 0) {
            String password = MD5Util.encrypt32(MD5Util.encrypt32(users.getUserName() + users.getPassword()));
            users.setPassword(password);
        }
        //支付密码加密
        if (payPwd != null && payPwd.length() != 0) {
            String SecurityPassword = MD5Util.encrypt32(MD5Util.encrypt32(users.getUserId() + users.getSecurityPassword()));
            users.setSecurityPassword(SecurityPassword);
        }
        Integer result = 0;
        //登录密码为空，支付密码不为空
        if ((pwd == null || pwd == "") && pwd.length() == 0 && payPwd != null) {
            result = usersMapper.updateSecurityPassword(users.getSecurityPassword(), users.getUserId());
            if (result > 0) {
                return usersMapper.UpdateRoleId(users.getRoleId(), users.getUserId());
            }
        } else
            //登录密码不为空，支付密码为空
            if ((pwd != "" || pwd != null) && pwd.length() != 0 && payPwd.length() == 0) {
                result = usersMapper.updatePassword(users.getPassword(), users.getUserId());
                if (result > 0) {
                    return usersMapper.UpdateRoleId(users.getRoleId(), users.getUserId());
                }
            } else
                //两者都不为空
                if (pwd != null && pwd.length() != 0 && payPwd != null && payPwd.length() != 0) {
                    result = usersMapper.updatePassword(users.getPassword(), users.getUserId());
                    if (result > 0) {
                        result = usersMapper.updateSecurityPassword(users.getSecurityPassword(), users.getUserId());
                        if (result > 0) {
                            return usersMapper.UpdateRoleId(users.getRoleId(), users.getUserId());
                        }
                    }
                } else if (pwd.length() == 0 && payPwd.length() == 0) {
                    return usersMapper.UpdateRoleId(users.getRoleId(), users.getUserId());
                }
        return -1;
    }

    /**
     * 新增用户(后台管理) Timor 2019-2-23 16:32:11
     *
     * @param users
     * @return
     */
    @Override
    @Transactional
    public Integer insertUsers(Users users) {
        List<Users> usersList = usersMapper.selectByUserName(users.getUserName());
        if (usersList.size() > 0) {
            return -2;
        } else {
            users.setPassword(MD5Util.encrypt32(MD5Util.encrypt32(users.getUserName() + users.getPassword())));
            users.setSecurityPassword(MD5Util.encrypt32(MD5Util.encrypt32(users.getUserId() + users.getSecurityPassword())));
            Integer result = usersMapper.insertUsers(users);
            if (result > 0) {
                result = usersMapper.insertPermissionRelation(users.getUserId(), users.getRoleId());
                return result;
            }
        }
        return -1;
    }

    /**
     * 根据微信的openid 获取用户信息 狗蛋 2019年2月23日15:41:14
     *
     * @param openId
     * @return
     */
    @Override
    public Users getUserByOpenId(String openId) {
        return usersMapper.getUserByOpenId(openId);
    }

    /**
     * 用户注册（微信）
     *
     * @param wechatResp
     * @return
     */
    @Override
    @Transactional
    public Users registerWechat(WechatResp wechatResp) {
        if (wechatResp == null) throw new MsgException("微信信息不能为空！");
        // 初始化用户信息
        Users users = new Users();
        users.setPhoto(wechatResp.getHeadimgurl());
        users.setOpenId(wechatResp.getOpenid());
        users.setNickName(wechatResp.getNickname());
        users.setUnionid(wechatResp.getUnionid());
        users.setRoleId(4);
        users.setLastTime(new Date());
        users.setAddTime(new Date());
        users.setState(0);
        // 加入数据库
        Integer result = usersMapper.registerWechat(users);
        if (result <= 0) throw new MsgException("用户注册失败！");
        // 开通钱包
        Wallets wallets = new Wallets();
        wallets.setUserId(users.getUserId());
        wallets.setBalance(0D);
        wallets.setUpdateTime(new Date());
        wallets.setVersion(0);
        int count = 0;
        count = walletMapper.insert(wallets);
        if (count == 0) {
            throw new MsgException("开通钱包失败");
        }
        // 初始化用户权限信息
        PermissionRelation permissionRelation = new PermissionRelation();
        permissionRelation.setUid(users.getUserId());
        permissionRelation.setPermissionList(4);
        // 注册数据库
        int insert = permissionRelationMapper.insert(permissionRelation);
        if (insert <= 0) throw new MsgException("初始化用户权限失败！");
        return users;
    }

    /**
     * 设置支付密码(存在：修改支付密码 不存在：设置原始初始密码)   Timor  2019-2-25 14:57:44
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    @Transactional
    public Integer changePayPassword(HttpServletRequest request, String oldPassword, String newPassword) {

        //session中获取用户
        Integer userId = SessionUtil.getSession(request).getUserId();

        oldPassword = MD5Util.encrypt32(MD5Util.encrypt32(userId + oldPassword));
        //根据前台传递参数，判断原始支付密码是否存在
        if (newPassword == null || newPassword == "" || newPassword.length() == 0) {
            //对支付密码加盐加密
            return usersMapper.updateSecurityPassword(oldPassword, userId);
        } else {
            //判断输入的原始支付密码是否存在
            Users users = usersMapper.slectUserById(userId);
            if (users.getSecurityPassword().equals(oldPassword)) {
                newPassword = MD5Util.encrypt32(MD5Util.encrypt32(userId + newPassword));
                return usersMapper.updateSecurityPassword(newPassword, userId);
            }
            return -2;//输入的原始密码不正确
        }
    }

    /**
     * 修改登录密码   Timor   2019-2-25 14:57:30
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public Integer changePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        Users users = usersMapper.slectUserById(SessionUtil.getSession(request).getUserId());
        //对输入的旧密码，新密码进行加盐加密
        oldPassword = MD5Util.encrypt32(MD5Util.encrypt32(users.getUserName() + oldPassword));
        newPassword = MD5Util.encrypt32(MD5Util.encrypt32(users.getUserName() + newPassword));
        //判断输入的旧密码是否存在
        if (users.getPassword().equals(oldPassword)) {
            return usersMapper.updatePassword(newPassword, users.getUserId());
        } else {
            return -2; //旧密码输入不正确
        }
    }


    /**
     * 查询用户钱包信息 Timor  2019-2-26 17:14:00
     *
     * @param userId
     * @return
     */
    @Override
    public Wallets queryWallets(Integer userId) {
        return walletMapper.selectByUid(userId);
    }

    /**
     * 验证用户支付密码 狗蛋 2019年2月27日14:21:48
     *
     * @param userId
     * @param payPassword
     * @return
     */
    @Override
    public Boolean verifyPayPassword(Integer userId, String payPassword) {
        String newPassword = MD5Util.encrypt32(MD5Util.encrypt32(userId + payPassword));
        Users users = usersMapper.slectUserById(userId);
        if (StringUtils.isBlank(users.getSecurityPassword())) throw new MsgException("支付密码为设置！");
        if (!StringUtils.equals(newPassword, users.getSecurityPassword())) return false;
        return true;
    }

    /**
     * 根据微信唯一标识获取用户
     *
     * @param unionid
     * @return
     */
    public Users selectUserByUnionid(String unionid) {
        return usersMapper.selectUserByUnionid(unionid);
    }

    /**
     * app用户注册
     *
     * @param phone
     * @param password
     * @param code
     */
    @Override
    public Users registerApp(String phone, String password, String code) {
        // 校验短信验证码是否正确或过期
        String dbSmsCode = (String) redisTemplate.opsForValue().get("sms-" + phone);
        if (dbSmsCode == null || !dbSmsCode.equalsIgnoreCase(code)) throw new MsgException("短信验证码不正确");
        Users users = new Users();
        users.setPassword(password);
        users.setUserName(phone);
        users.setContactWay(phone);

        return register(users);
    }

    /**
     * 找回密码
     *
     * @param phone
     * @param password
     * @return
     */
    @Override
    public Integer findPassword(String phone, String password, String code) {
        // 校验短信验证码是否正确或过期
        String dbSmsCode = (String) redisTemplate.opsForValue().get("sms-" + phone);
        System.out.println("缓存中的验证码为：" + dbSmsCode);
        if (dbSmsCode == null || !dbSmsCode.equalsIgnoreCase(code)) throw new MsgException("短信验证码不正确");
        // 加盐加密用户密码
        String newPassword = MD5Util.encrypt32(MD5Util.encrypt32(phone + password));

        return usersMapper.updateUserPassword(newPassword, phone, phone);
    }
}
