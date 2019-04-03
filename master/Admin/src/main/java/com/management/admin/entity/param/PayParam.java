/***
 * @pName management
 * @name PayParam
 * @user DF
 * @date 2018/8/16
 * @desc 交易专用参数类
 */
package com.management.admin.entity.param;

import lombok.*;

import java.util.Date;

/**
 * 财务交易记录
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PayParam {
    /**
     * A用户（交易主体）
     */
    private Integer fromUid;
    /**
     * B用户（交易对方）
     */
    private Integer toUid;
    /**
     * 渠道类型
     */
    private Integer channelType;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 商品类型
     */
    private Integer productType;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 交易类型
     */
    private Integer tradeType;
    /**
     * 交易名称
     */
    private String tradeName;
    /**
     * 总额
     */
    private Double amount;
    /**
     * 备注（摘要）
     */
    private String remark;
    /**
     * 发生时间
     */
    private Date addTime;
    /**
     * 系统交易流水单号
     */
    private long systemRecordId;
    /**
     * 货币
     */
    private Integer currency = 0;
    /**
     * 渠道交易流水单号
     */
    private String channelRecordId;
    /**
     * 状态(0=正常,1=退款)
     */
    private Integer status;
    /**
     * 渠道交易到账响应时间
     */
    private Date toAccountTime;
}
