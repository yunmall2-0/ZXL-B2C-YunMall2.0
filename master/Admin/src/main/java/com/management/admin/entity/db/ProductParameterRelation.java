package com.management.admin.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 商品参数关系表
 */
@Table(name ="product_parameter_relation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductParameterRelation {
    /**
     * 编号
     */
    private Integer id;
    /**
     * 参数编号
     */
    private Integer productParameterId;
    /**
     * 商品编号
     */
    private Long productId;

    /**
     * 参数值
     */
    private String parameterValue;
}
