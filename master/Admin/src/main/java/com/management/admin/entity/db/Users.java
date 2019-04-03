package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName Users
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/21 16:33
 * Version 1.0
 **/

@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    private Integer userId;

    private String userName;

    private String password;

    private String securityPassword;

    private Integer roleId;

    private Date lastTime;

    private String contactWay;

    private Integer state;

    private Date addTime;

    private String openId;

    private String photo;

    private String unionid;

    private String nickName;
}
