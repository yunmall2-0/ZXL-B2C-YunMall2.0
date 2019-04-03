package com.management.admin.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

@Table(name ="products")
@Data
@AllArgsConstructor
public class Product {
    /**
     * 商品编号
     */
    private Long productId;
    /**
     * 所属商家
     */
    private Integer shopId;
    /**
     * 商品类型
     */
    private Integer type;
    /**
     * 商品主图
     */
    private String mainImage;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品价格
     */
    private Double amount;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 商品状态
     */
    private Integer status;
    /**
     * 添加时间
     */
    private Date addTime;
    /**
     * 最后一次修改时间
     */
    private Date editTime;
    /**
     * 商品摘要
     */
    private String remark;
    /**
     * 商品权重排序
     */
    private Integer sort;
    /**
     * 是否为热推商品
     */
    private Boolean isHot;
    /**
     * 商品分类编号
     */
    private Integer category;

    public Product(Long productId){
        this.productId = productId;
    }
    public Product(){ }
}
