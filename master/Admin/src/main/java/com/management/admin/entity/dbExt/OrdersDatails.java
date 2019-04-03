package com.management.admin.entity.dbExt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName OrdersDatails
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/26 15:36
 * Version 1.0
 **/



@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDatails {

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

    private String tradeTypeStr;
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

    private String addTimeStr;
    /**
     *修改时间
     */
    private Date editTime;

    private String editTimeStr;
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
     *卡密或链接ID
     */
    private Integer linkId;

    /**
     * 商品主图
     */
    private String mainImage;

    private String statusStr;

    private String productName;

    private Integer type;

    private String link;

    private String cardNumber;

    private String cardPwd;

    List<KhLinks> khLinks;

}
