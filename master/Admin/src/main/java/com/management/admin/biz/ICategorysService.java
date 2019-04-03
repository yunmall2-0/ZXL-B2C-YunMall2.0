package com.management.admin.biz;

import com.management.admin.entity.db.Categorys;
import com.management.admin.entity.resp.TreeWrappage;

import java.util.List;

public interface ICategorysService {
    /**
     * 查询所有类目 钉子 2019年2月25日11:22:26
     * @return
     */
    List<Categorys> selectAllCategorys();
    /**
     * 查询所有的一级分类 钉子 2019年2月25日11:18:24
     * @return
     */
    List<Categorys> selectAllOneCategorys();

    /**
     * 查询所有二级分类 钉子 2019年2月27日09:58:11
     * @return
     */
    List<Categorys> selectAllTwoCategorys();

    /**
     * 一对多查询一级和二级分类 钉子 2019年2月28日13:50:27
     * @return
     */
    List<Categorys> selectAllOneCategorysAndTwo();

    /**
     * 根据一级分类ID查询子分类 钉子 2019年2月25日11:18:50
     * @param categoryId
     * @return
     */
    List<Categorys> selectAllCategorysById(Integer categoryId);

    /**
     * 根据分类名模糊查询分类  钉子 2019年2月25日13:26:20
     * @param categoryName
     * @return
     */
    List<Categorys> selectAllCategoryByName(String categoryName);

    /**
     * 根据ID获取分类信息 钉子 2019年2月25日13:51:34
     * @param categoryId
     * @return
     */
    Categorys selectCategoryById(Integer categoryId);

    /**
     * 添加分类 钉子 2019年2月25日11:36:12
     * @param categorys
     * @return
     */
    Integer insertCategory(Categorys categorys);

    /**
     * 修改分类  钉子 2019年2月25日11:47:49
     * @param categorys
     * @return
     */
    Integer updateCategory(Categorys categorys);

    /**
     * 删除分类  钉子 2019年2月25日11:59:14
     * @param categoryId
     * @return
     */
    Integer deleteCategory(Integer categoryId);

    /**
     * 删除一级分类和二级分类  钉子 2019年2月25日13:25:02
     * @param categoryId
     * @return
     */
    Integer deleteCategoryAndTwo(Integer categoryId);

    /**
     * 获取后台类目列表 狗蛋 2019年2月26日11:13:38
     *
     * @param sourceList 原数据列表
     * @param categoryId
     * @return
     */
    List<TreeWrappage> convertTree(List<Categorys> sourceList, Integer categoryId);
}
