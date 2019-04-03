package com.management.admin.biz.impl;

import com.management.admin.biz.IPermissionRelationService;
import com.management.admin.entity.db.PermissionRelation;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.dbExt.PermissionRelationDetail;
import com.management.admin.repository.PermissionRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionRelationServiceImpl extends BaseServiceImpl<PermissionRelation> implements IPermissionRelationService {

    private PermissionRelationMapper permissionRelationMapper;

    @Autowired
    public PermissionRelationServiceImpl(PermissionRelationMapper permissionRelationMapper) {
        this.permissionRelationMapper = permissionRelationMapper;
    }

    /**
     * 获取数据  2018年8月13日13:26:57
     *
     * @param param
     * @return
     */
    @Override
    public PermissionRelation get(PermissionRelation param) {
        return super.get(param);
    }

    /**
     * 获取全部数据  2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<PermissionRelation> getList() {
        return super.getList();
    }

    /**
     * 插入数据  2018年8月13日13:27:48
     *
     * @param param
     * @return
     */
    @Override
    public int insert(PermissionRelation param) {
        return super.insert(param);
    }

    /**
     * 更新数据  2018年8月13日13:28:01
     *
     * @param param
     * @return
     */
    @Override
    public int update(PermissionRelation param) {
        return super.update(param);
    }

    /**
     * 删除数据  2018年8月13日13:28:16
     *
     * @param param
     * @return
     */
    @Override
    public int delete(PermissionRelation param) {
        return super.delete(param);
    }




    @Override
    public List<PermissionRelationDetail> getListByUserId(Integer userId) {
        return permissionRelationMapper.selectByUserId(userId);
    }
}
