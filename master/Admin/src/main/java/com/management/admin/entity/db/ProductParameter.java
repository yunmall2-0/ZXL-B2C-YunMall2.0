package com.management.admin.entity.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Table(name ="product_parameters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductParameter {

    private Integer id;

    private String name;

    /**
     * 分类编号（绑定参数的分类名称）
     */
    private Integer categoryId;
}
