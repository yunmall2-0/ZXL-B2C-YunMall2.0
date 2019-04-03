package com.management.admin.entity.dbExt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRelationDetail {
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

    /**
     * 权限编号
     */
    private Integer permissionId;
    /**
     * 权限名称
     */
    private String roleName;
    /**
     * 摘要（备注）
     */
    private String remark;
}
