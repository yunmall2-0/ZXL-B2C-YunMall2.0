package com.management.admin.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Table(name="Permission_Relations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRelation {

    /**
     * 关系编号
     */
    private Integer relationId;
    /**
     * 用户编号
     */
    private Integer uid;
    /**
     * 权限编号
     */
    private Integer permissionList;
}
