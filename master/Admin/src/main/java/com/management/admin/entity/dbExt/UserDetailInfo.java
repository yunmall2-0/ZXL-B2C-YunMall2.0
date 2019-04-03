/***
 * @pName management
 * @name UserWalletDetail
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.entity.dbExt;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailInfo {
    /*users*/
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

    private Integer walletId;
    private Double balance;
    private Date updateTime;
    private Integer version;

    /*permission relation*/
    private String permissionRole;
}
