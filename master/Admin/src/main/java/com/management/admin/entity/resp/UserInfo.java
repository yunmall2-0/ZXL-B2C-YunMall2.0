package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Integer userId;

    private String userName;

    private String contactWay;

    private String token;

    private String roleName;
}
