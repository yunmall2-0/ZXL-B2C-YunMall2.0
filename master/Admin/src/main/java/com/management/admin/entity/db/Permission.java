package com.management.admin.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 权限
 */
@Table(name="permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
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
