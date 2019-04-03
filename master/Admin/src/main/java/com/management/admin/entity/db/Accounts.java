package com.management.admin.entity.db;

/***
 * @pName management
 * @name Accounts
 * @user HongWei
 * @date 2018/8/15
 * @desc 财务会计账目表
 */

import lombok.*;
import tk.mybatis.mapper.annotation.KeySql;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {
    @Id
    private Long accountsId;
    private Long payId;
    private Integer tradeAccountId;
    private String tradeAccountName;
    private Integer accountsType;
    private Integer currency;
    private Double amount;
    private Double beforeBalance;
    private Double afterBalance;
    private Date addTime;
    private String remark;
}
