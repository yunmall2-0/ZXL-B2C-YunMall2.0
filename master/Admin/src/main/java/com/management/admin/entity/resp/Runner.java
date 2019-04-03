package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runner {
    /**
     * 轮播图片
     */
    private String image;
    /**
     * 绑定的商品编号
     */
    private String productIdStr;
    /**
     * 绑定的商品名称
     */
    private String productName;

    /**
     * 数据字典商品key
     */
    private String productKey;

    /**
     * 数据字典图片key
     */
    private String imageKey;
}
