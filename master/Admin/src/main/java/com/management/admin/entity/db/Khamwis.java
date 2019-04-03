package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "khamwis")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Khamwis {
    private Integer cardId;
    private String cardNumber;
    private String cardPwd;
    private Long productId;
    private Date getTime;
    private Integer status;
    private Date addTime;
    private Boolean isDelete;
}
