package com.management.admin.biz.impl;

import com.management.admin.biz.IPermissionService;
import com.management.admin.entity.db.Permission;
import com.management.admin.repository.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements IPermissionService {


    private PermissionMapper permissionMapper;

    @Autowired
    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * 获取数据  2018年8月13日13:26:57
     *
     * @param param
     * @return
     */
    @Override
    public Permission get(Permission param) {
        return super.get(param);
    }

    /**
     * 获取全部数据  2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<Permission> getList() {
        return permissionMapper.selectAll();
    }

    /**
     * 分页获取全部数据  2018年8月13日13:27:17
     *
     * @param page
     * @param limit
     * @param condition
     * @return
     */
    @Override
    public List<Permission> getLimit(Integer page, String  limit, String condition) {
        return super.getLimit(page, limit, condition);
    }

    /**
     * 插入数据  2018年8月13日13:27:48
     *
     * @param param
     * @return
     */
    @Override
    public int insert(Permission param) {
        return super.insert(param);
    }

    /**
     * 更新数据  2018年8月13日13:28:01
     *
     * @param param
     * @return
     */
    @Override
    public int update(Permission param) {
        return super.update(param);
    }

    /**
     * 删除数据  2018年8月13日13:28:16
     *
     * @param param
     * @return
     */
    @Override
    public int delete(Permission param) {
        return super.delete(param);
    }



}
