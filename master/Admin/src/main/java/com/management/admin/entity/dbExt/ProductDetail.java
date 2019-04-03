package com.management.admin.entity.dbExt;

import com.management.admin.entity.db.Khamwis;
import com.management.admin.entity.db.Links;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetail {

    private String productIdStr;

    private Long productId;

    private Integer shopId;

    private Integer type;

    private String mainImage;

    private String productName;

    private Double amount;

    private String description;

    private Integer status;

    private Date addTime;

    private String addTimeStr;

    private Date editTime;

    private String editTimeStr;

    private String remark;

    private Integer sort;

    private Boolean isHot;
    /**
     * 关联卡密
     */
    private List<Khamwis> khamwisList;

    /**
     * 库存数量
     */
    private Integer inventory;

    /**
     * 关联链接
     */
    private List<Links> linksList;

    /**
     * 商品分类编号
     */
    private Integer category;

    /**
     * 商品分类名称
     */
    private String categoryName;

    /**
     * 参数
     */
    private List<ProductParameterDetail>  parameterDetailList;
}
