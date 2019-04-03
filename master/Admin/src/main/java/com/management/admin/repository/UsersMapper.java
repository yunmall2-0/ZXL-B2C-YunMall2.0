package com.management.admin.repository;

import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Users;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Update;
import com.management.admin.entity.dbExt.UserDetailInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import java.util.List;

/**
 * @ClassName UsersMapper
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/21 16:31
 * Version 1.0
 **/

@Mapper
public interface  UsersMapper extends MyMapper<Users> {


    /**
     * 分页查询用户 Timor 2019-2-22 13:06:55
     * @param page
     * @param limit
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("SELECT * from users WHERE ${condition} ORDER BY add_time DESC LIMIT #{page},${limit}")
    List<Users> selectLimit(@Param("page") Integer page, @Param("limit") String limit
            , @Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);


    /**
     * 分页查询用户记录数 Timor 2019-2-22 13:09:09
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select count(user_id) from users where ${condition}")
    Integer selectLimitCount(@Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);

    /**
     * 根据ID 查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from users where user_Id=#{userId}")
    Users slectUserById(Integer userId);

    /**
     * 根据用户名查询未禁用的用户 狗蛋 2019年2月22日10:39:05
     * @param userName
     * @return
     */
    @Select("select * from users where user_Name=#{userName} and state=0")
    Users selectUserByUserName(String userName);

    /**
     * 新增用户(管理员及其他权限用户) Timor 2019-2-22 13:37:23
     * @param users
     * @return
     */
    @Insert("insert into users(user_name,password,security_password,role_id,contact_way,add_time,nick_name,photo)" +
            "  values (#{userName},#{password},#{securityPassword},#{roleId},#{contactWay},NOW(),#{nickName},#{photo})")
    @Options(useGeneratedKeys=true, keyProperty="userId", keyColumn="user_id")
    Integer insertUsers(Users users);

    /**
     * 添加相应的权限关系 Timor 2019-2-22 14:35:40
     * @param uid
     * @param permissionList
     * @return
     */
    @Insert("insert into permission_relations(uid,permission_List) values (#{uid},#{permissionList})")
    Integer insertPermissionRelation(@Param("uid") Integer uid,@Param("permissionList") Integer permissionList);

    /**
     * 修改用户密码 Timor 2019-2-22 14:42:03
     * @param password
     * @param userId
     * @return
     */
    @Update("update users set password=#{password} where user_id=#{userId}")
    Integer updatePassword(@Param("password") String password,@Param("userId") Integer userId);

    /**
     * 修改用户等级(关系表同步修改)
     * @param roleId
     * @param userId
     * @return
     */
    @Update("update users u,permission_relations p set u.role_id=#{roleId},p.permission_List=#{roleId} where u.user_id=p.uid and user_id=#{userId}")
    Integer  UpdateRoleId(@Param("roleId") Integer roleId,@Param("userId") Integer userId);


    /**
     * 修改用户支付密码 Timor 2019-2-22 14:45:11
     * @param securityPassword
     * @param userId
     * @return
     */
    @Update("update users set security_password=#{securityPassword} where user_id=#{userId}")
    Integer updateSecurityPassword(@Param("securityPassword") String securityPassword,@Param("userId") Integer userId);


   /**
     * 根据用户名查询用户 狗蛋 2019年2月22日10:39:05
     * @param userName
     * @return
     */
    @Select("select * from users where user_Name=#{userName}")
    List<Users> selectByUserName(String userName);

    /**
     * 修改用户最后一次登陆时间 狗蛋 2019年2月22日14:19:27
     * @param userId
     * @param lastTime
     * @return
     */
    @Update("update users set last_Time = #{lastTime} where user_id = #{userId}")
    Integer updateUserLastTime(@Param("userId") Integer userId ,@Param("lastTime") Date lastTime);

    /**
     * 用户登录（前台官网） 狗蛋 2019年2月23日09:50:11
     * @param userName
     * @param password
     * @return
     */
    @Select("select * from users where user_name=#{userName}" +
            " and password=#{password}")
    Users login(@Param("userName") String userName,@Param("password") String password);
    /**
     * 修改用户状态  Timor 2019-2-22 18:08:47
     * @param state
     * @param userId
     * @return
     */
    @Update("update users set state=#{state} where user_id = #{userId}")
    Integer updateState(@Param("state")Integer state,@Param("userId")Integer userId);


    /**
     * 查询多个交易主体信息 钉子 2019年2月22日14:10:51
     * @param userId
     * @return
     */
    @Select("SELECT t1.*,t2.* FROM users t1 LEFT JOIN wallets t2 ON t2.user_id = t1.user_id WHERE t1.user_id IN(${userId})")
    List<UserDetailInfo> selectInUid(@Param("userId") String userId);

    /**
     * 查询单个交易主体信息 钉子 2019年2月22日14:10:32
     * @param userId
     * @return
     */
    @Select("SELECT t1.*,t2.* FROM users t1 LEFT JOIN wallets t2 ON t2.user_id = t1.user_id WHERE t1.user_id =#{userId}")
    UserDetailInfo selectUserDetail(@Param("userId") String userId);

    /**
     * 根据微信openid获取用户信息
     * @param openId
     * @return
     */
    @Select("select * from users where open_id=#{openId}")
    Users getUserByOpenId(String openId);

    /**
     * 用户注册（微信） 狗蛋 2019年2月23日16:15:49
     * @param users
     * @return
     */
    @Insert("insert into users(state,role_id,add_time,last_time,open_id,user_name,photo,unionid) " +
            "values (#{state},#{roleId},#{addTime},#{lastTime},#{openId},#{userName}," +
            "#{photo},#{unionid})")
    @Options(useGeneratedKeys=true, keyProperty="userId", keyColumn="user_id")
    Integer registerWechat(Users users);

    /**
     * 根据唯一标识查找用户信息
     * @param unionid
     * @return
     */
    @Select("select * from users where unionid = #{unionid}")
    Users selectUserByUnionid(String unionid);

    /**
     * 修改用户密码
     * @param userName
     * @param contactWay
     * @return
     */
    @Update("update users set password=#{newPassword} where user_name=#{userName} and contact_Way=#{contactWay}")
    Integer updateUserPassword(@Param("newPassword") String newPassword,@Param("userName") String userName,@Param("contactWay") String contactWay);



}
