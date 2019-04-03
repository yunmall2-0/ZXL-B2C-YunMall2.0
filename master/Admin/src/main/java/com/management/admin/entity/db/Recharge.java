/***
 * @pName management
 * @name Withdraw
 * @user HongWei
 * @date 2018/8/21
 * @desc
 */
package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "recharge")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Recharge {
    @Id
    private Integer rechargeId;
    private Integer userId;
    private Double amount;
    private Integer state;
    private String remark;
    private Long systemRecordId;
    private String channelRecordId;
    private Date addTime;
    private Date updateTime;
}
