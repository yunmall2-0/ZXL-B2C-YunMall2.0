package com.management.admin.repository;

import com.management.admin.entity.db.Permission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends MyMapper<Permission>{
}
