package com.management.admin.entity.dbExt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数详情
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductParameterDetail {

    private Integer id;

    private String name;

    private String value;

    private Integer productParameterId;

    private Integer categoryId;

    private String categoryName;
}
