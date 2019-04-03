package com.management.admin.repository;

import com.management.admin.entity.db.PermissionRelation;
import com.management.admin.entity.dbExt.PermissionRelationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionRelationMapper extends MyMapper<PermissionRelation> {

    /**
     * 根据用户查询权限
     * @param userId
     * @return
     */
    @Select("select * from permission_Relations t1 left join" +
            " permissions t2 on t1.permission_List = t2.permission_Id where uid=#{userId}")
    List<PermissionRelationDetail> selectByUserId(Integer userId);
}
