package com.management.admin.repository;

import com.management.admin.entity.db.ProductParameterRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductParameterRelationMapper extends MyMapper<ProductParameterRelation>{


    /**
     * 查询
     * @param productId
     * @return
     */
    @Select("select * from product_parameter_relation where product_id=#{productId}")
    List<ProductParameterRelation> selectByProductId(Long productId);

    /**
     * 修改
     * @param parameterRelation
     * @return
     */
    @Update("update product_parameter_relation set product_Parameter_Id = #{productParameterId}," +
            "product_Id = #{productId},parameter_Value = #{parameterValue} where id = #{id}")
    Integer updateProductParameterRelationById(ProductParameterRelation parameterRelation);
}
