package com.management.admin.repository;

import com.management.admin.entity.db.Categorys;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategorysMapper extends MyMapper<Categorys>{
    /**
     * 查询所有类目 钉子 2019年2月25日11:22:26
     * @return
     */
    @Select("select * from categorys where is_delete=0 ORDER BY sort DESC")
    List<Categorys> selectAllCategorys();
    /**
     * 查询所有的一级分类 钉子 2019年2月25日11:18:24
     * @return
     */
    @Select("select * from categorys where is_delete=0 and parent_id=0 ORDER BY sort DESC")
    List<Categorys> selectAllOneCategorys();
    /**
     * 查询所有二级分类 钉子 2019年2月27日09:58:11
     * @return
     */
    @Select("select * from categorys where is_delete=0 and parent_id>0 ORDER BY sort DESC")
    List<Categorys> selectAllTwoCategorys();

    /**
     * 一对多查询一级和二级分类 钉子 2019年2月28日13:50:27
     * @return
     */
    @Select("select * from categorys where is_delete=0 and parent_id=0 ORDER BY sort DESC")
    @Results({
            @Result(property = "categorys", column = "category_id",many = @Many(select = "com.management.admin.repository.CategorysMapper.selectAllCategorysById"))
    })
    List<Categorys> selectAllOneCategorysAndTwo();

    /**
     * 根据一级分类ID查询子分类 钉子 2019年2月25日11:18:50
     * @param categoryId
     * @return
     */
    @Select("select * from categorys where is_delete=0 and parent_id=#{categoryId} ORDER BY sort DESC")
    List<Categorys> selectAllCategorysById(Integer categoryId);

    /**
     * 根据分类名模糊查询分类 钉子 2019年2月25日13:23:23
     * @param categoryName
     * @return
     */
    @Select("select * from categorys where category_name like '%${categoryName}%' and is_delete=0 ORDER BY sort DESC")
    List<Categorys> selectAllCategoryByName(@Param("categoryName") String categoryName);

    /**
     * 根据ID获取分类信息 钉子 2019年2月25日13:51:34
     * @param categoryId
     * @return
     */
    @Select("select * from categorys where category_id=#{categoryId} and is_delete=0")
    Categorys selectCategoryById(Integer categoryId);

    /**
     * 添加分类 钉子 2019年2月25日11:36:12
     * @param categorys
     * @return
     */
    @Insert("insert into categorys(parent_id,category_name,is_leaf,sort) " +
            "values(#{parentId},#{categoryName},#{isLeaf},#{sort})")
    Integer insertCategory(Categorys categorys);

    /**
     * 修改分类  钉子 2019年2月25日11:47:49
     * @param categorys
     * @return
     */
    @Update("update categorys set category_name=#{categoryName},sort=#{sort} where category_id=#{categoryId}")
    Integer updateCategory(Categorys categorys);

    /**
     * 删除分类  钉子 2019年2月25日11:59:14
     * @param categoryId
     * @return
     */
    @Update("update categorys set is_delete=1 where category_id=#{categoryId}")
    Integer deleteCategory(Integer categoryId);

    /**
     * 删除一级分类和二级分类  钉子 2019年2月25日13:25:02
     * @param categoryId
     * @return
     */
    @Update("update categorys set is_delete=1 where category_id=#{categoryId} or parent_id=#{categoryId}")
    Integer deleteCategoryAndTwo(Integer categoryId);
}
