package com.management.admin.biz.impl;

import com.management.admin.biz.ICategorysService;
import com.management.admin.entity.db.Categorys;
import com.management.admin.entity.resp.TreeWrappage;
import com.management.admin.repository.CategorysMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorysServiceImpl implements ICategorysService {
    private final CategorysMapper categorysMapper;

    @Autowired
    public CategorysServiceImpl(CategorysMapper categorysMapper) {
        this.categorysMapper = categorysMapper;
    }

    /**
     * 查询所有的分类  钉子 2019年2月25日13:05:04
     *
     * @return
     */
    @Override
    public List<Categorys> selectAllCategorys() {
        return categorysMapper.selectAllCategorys();
    }

    /**
     * 查询所有的一级分类 钉子 2019年2月25日13:07:13
     *
     * @return
     */
    @Override
    public List<Categorys> selectAllOneCategorys() {
        return categorysMapper.selectAllOneCategorys();
    }

    /**
     * 查询所有的二级分类  钉子 2019年2月27日13:51:38
     * @return
     */
    @Override
    public List<Categorys> selectAllTwoCategorys() {
        return categorysMapper.selectAllTwoCategorys();
    }

    /**
     * 一对多查询一级和二级分类 钉子 2019年2月28日13:52:34
     * @return
     */
    @Override
    public List<Categorys> selectAllOneCategorysAndTwo() {
        return categorysMapper.selectAllOneCategorysAndTwo();
    }

    /**
     * 根据一级分类ID查询叶子级分类  钉子 2019年2月25日13:08:11
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Categorys> selectAllCategorysById(Integer categoryId) {
        return categorysMapper.selectAllCategorysById(categoryId);
    }


    /**
     * 根据分类名模糊查询分类 钉子 2019年2月25日13:27:22
     *
     * @param categoryName
     * @return
     */
    @Override
    public List<Categorys> selectAllCategoryByName(String categoryName) {

        return categorysMapper.selectAllCategoryByName(categoryName);
    }

    /**
     * 根据ID查询分类信息 钉子 2019年2月25日13:52:24
     *
     * @param categoryId
     * @return
     */
    @Override
    public Categorys selectCategoryById(Integer categoryId) {
        return categorysMapper.selectCategoryById(categoryId);
    }

    /**
     * 添加分类 钉子 2019年2月25日13:08:43
     *
     * @param categorys
     * @return
     */
    @Override
    public Integer insertCategory(Categorys categorys) {
        if (categorys.getParentId() == 0) {
            categorys.setIsLeaf(false);
        } else {
            categorys.setIsLeaf(true);
        }
        return categorysMapper.insertCategory(categorys);
    }

    /**
     * 修改分类 钉子 2019年2月25日13:09:13
     *
     * @param categorys
     * @return
     */
    @Override
    public Integer updateCategory(Categorys categorys) {
        return categorysMapper.updateCategory(categorys);
    }

    /**
     * 删除分类 钉子 2019年2月25日13:09:40
     *
     * @param categoryId
     * @return
     */
    @Override
    public Integer deleteCategory(Integer categoryId) {
        return categorysMapper.deleteCategory(categoryId);
    }

    /**
     * 删除一级分类和二级分类 钉子 2019年2月25日13:27:58
     *
     * @param categoryId
     * @return
     */
    @Override
    public Integer deleteCategoryAndTwo(Integer categoryId) {
        return categorysMapper.deleteCategoryAndTwo(categoryId);
    }

    /**
     * 获取后台类目列表 狗蛋 2019年2月26日11:13:38
     *
     * @param sourceList 原数据列表
     * @param categoryId
     * @return
     */
    @Override
    public List<TreeWrappage> convertTree(List<Categorys> sourceList, Integer categoryId) {
        List<TreeWrappage> targetList = new ArrayList<>();
        List<Categorys> depth = sourceList.stream().filter(item -> item.getParentId().equals(categoryId)).collect(Collectors.toList());
        depth.forEach(item -> {
            targetList.add(new TreeWrappage(item.getCategoryId()
                    , item.getParentId()
                    , item.getCategoryName()
                    , item.getIsLeaf() == true
                    , item.getIsDelete() == false
                    , item.getSort()
                    , convertTree(sourceList, item.getCategoryId())));
        });
        return targetList;
    }
}
