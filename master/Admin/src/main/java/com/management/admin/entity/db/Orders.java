package com.management.admin.entity.db;


import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName Orders
 * @Description 订单类
 * @Author Timor
 * @Date 2019/2/20 9:49
 * Version 1.0
 **/

@Table(name = "orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    /**
     *订单号
     */
    private String orderId;
    /**
     *父类订单号
     */
    private String parentId;
    /**
     *商品Id
     */
    private Long pid;
    /**
     *商品名称
     */
    private String pName;
    /**
     *商家Id
     */
    private Integer sid;
    /**
     *商家名称
     */
    private String sName;
    /**
     *用户Id
     */
    private Integer uid;
    /**
     *用户姓名
     */
    private String uName;
    /**
     *支付方式（0：站内 1：支付宝 2：微信）
     */
    private Integer tradeType;
    /**
     * 商品单价
     */
    private Double price;
    /**
     * 商品数量
     */
    private Integer number;
    /**
     *金额
     */
    private Double amount;
    /**
     *流水号
     */
    private String  payId;
    /**
     *创建时间
     */
    private Date addTime;
    /**
     *修改时间
     */
    private Date editTime;
    /**
     *摘要
     */
    private String remark;
    /**
     *状态（0=待支付,1=待发货,2=已发货，3=已收货,4=已关闭,5=异常关闭）
     */
    private Integer status;
    /**
     *手机号
     */
    private String  phone;
    /**
     * 联系方式
     */
    private String contact;
    /**
     *卡密或链接ID
     */
    private String linkId;

}
