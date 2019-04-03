package com.management.admin.entity.dbExt;


import lombok.*;

import java.util.Date;

/**
 * @ClassName OrderDTO
 * @Description 订单
 * @Author Timor
 * @Date 2018/12/22 10:38
 * Version 1.0
 **/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 订单名称
     */
    private String payName;


    /**
     * 微信openId
     */
    private String buyerOpenid;

    /**
     * 金额
     */
    private Double orderAmount;

    /**
     * 场景信息
     */
    private String sceneInfo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

}
