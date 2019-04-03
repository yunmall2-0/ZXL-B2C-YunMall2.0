package com.management.admin.repository;

import com.management.admin.entity.db.ProductParameter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductParameterMapper extends MyMapper<ProductParameter> {


    /**
     * 获取列表
     * @param condition
     * @param page
     * @param limit
     * @return
     */
    @Select("SELECT * from product_parameters WHERE ${condition}  LIMIT #{page},${limit}")
    List<ProductParameter> selectLimit(@Param("condition") String condition,
                                       @Param("page") Integer page,
                                       @Param("limit") String limit);
    /**
     * 查询分页记录总数 Timor 2019年2月20日10:32:47
     * @param condition
     * @return
     */
    @Select("select count(id) from product_parameters where ${condition}")
    Integer selectLimitCount( @Param("condition") String condition);


    /**
     * 查询总记录数 Timor 2019年2月20日10:32:47
     * @return
     */
    @Select("select count(id) from product_parameters")
    Integer getCount();
    /**
     * 根据编号查询
     * @param id
     * @return
     */
    @Select("select * from product_parameters where id=#{id}")
    ProductParameter selectById(Integer id);

    /**
     * 根据分类编号查询
     * @param categoryId
     * @return
     */
    @Select("select * from product_parameters where category_Id = #{categoryId}")
    List<ProductParameter> selectByCategoryId(Integer categoryId);

    /**
     * 修改
     * @param parameter
     * @return
     */
    @Update("update product_parameters set name=#{name},category_id=#{categoryId}" +
            " where id=#{id}")
    Integer update(ProductParameter parameter);
}
