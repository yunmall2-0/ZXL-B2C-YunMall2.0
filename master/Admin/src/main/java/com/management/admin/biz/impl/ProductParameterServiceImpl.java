package com.management.admin.biz.impl;

import com.alibaba.fastjson.JSON;
import com.management.admin.biz.IProductParameterService;
import com.management.admin.entity.db.Categorys;
import com.management.admin.entity.db.Product;
import com.management.admin.entity.db.ProductParameter;
import com.management.admin.entity.dbExt.ProductParameterDetail;
import com.management.admin.repository.CategorysMapper;
import com.management.admin.repository.ProductParameterMapper;
import com.management.admin.repository.utils.ConditionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductParameterServiceImpl implements IProductParameterService {


    private ProductParameterMapper parameterMapper;
    private CategorysMapper categorysMapper;

    @Autowired
    public ProductParameterServiceImpl(ProductParameterMapper parameterMapper, CategorysMapper categorysMapper) {
        this.parameterMapper = parameterMapper;
        this.categorysMapper = categorysMapper;
    }

    /**
     * 分页加载 Timor 2019-2-20 13:18:26
     *
     * @param page
     * @param limit
     * @param condition
     * @return
     */
    @Override
    public List<ProductParameterDetail> getLimit(Integer page, String limit, String condition,Integer categoryId) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        // 拼接查询条件
        String where = extractLimitWhere(condition,categoryId);
        // 查询出的源集合
        List<ProductParameter> sourceList = parameterMapper.selectLimit(where, page, limit);
        // 新集合（要返回的）
        List<ProductParameterDetail> list = new ArrayList<>();
        // 遍历源集合
        sourceList.forEach(item -> {
            // 查询所绑定的分类
            ProductParameterDetail productParameterDetail = new ProductParameterDetail();
            // 克隆属性值
            BeanUtils.copyProperties(item, productParameterDetail);
            // 查询二级
            Categorys twoCategory = categorysMapper.selectCategoryById(item.getCategoryId());
            if (twoCategory != null) {
                // 查询一级
                Categorys oneCategory = categorysMapper.selectCategoryById(twoCategory.getParentId());
                // 绑定分类名称
                productParameterDetail.setCategoryName(oneCategory.getCategoryName()+"-->>>"+twoCategory.getCategoryName());
            }
            list.add(productParameterDetail);
        });
        return list;
    }


    /**
     * 加载分页记录数 Timor 2019-2-20 13:19:08
     *
     * @param condition
     * @return
     */
    @Override
    public Integer getLimitCount(String condition,Integer categoryId) {
        String where = extractLimitWhere(condition,categoryId);

        return parameterMapper.selectLimitCount(where);
    }

    public Integer getCount() {
        return parameterMapper.getCount();
    }

    /**
     * 添加
     *
     * @param name       名称
     * @param categoryId 分类编号
     * @return
     */
    @Override
    public Integer add(String name, Integer categoryId) {
        ProductParameter parameter = new ProductParameter();
        parameter.setCategoryId(categoryId);
        parameter.setName(name);
        return parameterMapper.insert(parameter);
    }

    /**
     * 提取分页条件
     *
     * @return
     */
    private String extractLimitWhere(String condition,Integer categoryId) {
        // 查询模糊条件
        String where = " 1=1";
        if(categoryId != null){
            where +=" AND category_id=" +categoryId;
        }
        if (StringUtils.isNotEmpty(condition)) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("name", condition);

            where += " OR " + ConditionUtil.like("id", condition) + ")";
        }
        return where.trim();
    }


    /**
     * 根据编号查询
     *
     * @param id
     * @return
     */
    @Override
    public ProductParameter selectById(Integer id) {
        return parameterMapper.selectById(id);
    }

    /**
     * 修改
     *
     * @param parameter
     * @return
     */
    @Override
    public Integer updateParameter(ProductParameter parameter) {
        return parameterMapper.update(parameter);
    }

    /**
     * 删除（真删）
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteParameter(Integer id) {
        ProductParameter parameter = new ProductParameter();
        parameter.setId(id);
        return parameterMapper.delete(parameter);
    }

    /**
     * 根据分类编号查询相应的所绑定的商品参数
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<ProductParameter> selectByCategoryId(Integer categoryId) {
        return parameterMapper.selectByCategoryId(categoryId);
    }
}
