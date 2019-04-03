package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @ClassName examine
 * @Description 提现审批记录
 * @Author ZXL01
 * @Date 2019/3/1 13:34
 * Version 1.0
 **/

@Table(name = "examine")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Examine {

    private Integer shenpiId;

    private Integer userId;

    private String cashAccount;

    private String realName;

    private Double money;

    private Long systemRecordId;

    private Integer status;

    private Date createTime;

    private String remark;

    private String  channelRecordId;

    private Date updateTime;
    /**
     * String 流水号(js前台转换)
     */
    @Transient // 表示表中没有此字段
    private String strPayId;
}
