package com.management.admin.biz;

import com.management.admin.entity.db.PermissionRelation;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.dbExt.PermissionRelationDetail;

import java.util.List;

public interface IPermissionRelationService extends IBaseService<PermissionRelation> {

    /**
     * 根据用户编号
     * @param userId
     * @return
     */
    List<PermissionRelationDetail> getListByUserId(Integer userId);
}
