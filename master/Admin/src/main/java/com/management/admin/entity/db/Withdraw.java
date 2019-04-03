package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName withdraw
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/28 16:59
 * Version 1.0
 **/

@Table(name = "withdraw")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Withdraw {

    private Integer withdrawId;

    private Integer userId;

    private String cashAccount;

    private String realName;

    private Date addTime;


}
