package com.management.admin.biz;

import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.ProductParameter;
import com.management.admin.entity.dbExt.ProductParameterDetail;
import com.management.admin.repository.utils.ConditionUtil;

import java.util.List;

public interface IProductParameterService {


    /**
     * 查询分页
     * @param page
     * @param limit
     * @param condition

     * @return
     */
    List<ProductParameterDetail> getLimit(Integer page, String limit, String condition,Integer categoryId);

    /**
     * 查询分页记录数
     * @param condition

     * @return
     */
    Integer getLimitCount(String condition,Integer categoryId);

    /**
     * 查询分页总记录数
     * @return
     */
    Integer getCount();

    /**
     * 添加
     * @param name   名称
     * @param categoryId 分类编号
     * @return
     */
    Integer add(String name,Integer categoryId);

    /**
     * 根据编号查询
     * @param id
     * @return
     */
    ProductParameter selectById(Integer id);

    /**
     * 修改
     * @param parameter
     * @return
     */
    Integer updateParameter(ProductParameter parameter);

    /**
     * 删除（真删）
     * @param id
     * @return
     */
    Integer deleteParameter(Integer id);

    /**
     * 根据分类编号查询相应的所绑定的商品参数
     * @param categoryId
     * @return
     */
    List<ProductParameter> selectByCategoryId(Integer categoryId);
}
