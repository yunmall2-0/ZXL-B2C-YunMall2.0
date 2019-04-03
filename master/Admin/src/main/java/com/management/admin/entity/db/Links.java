package com.management.admin.entity.db;

import io.swagger.models.auth.In;
import lombok.*;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "links")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Links {
    private Integer linkId;
    private String link;
    private Long productId;
    private Date getTime;
    private Integer status;
    private Date addTime;
    private Boolean isDelete;
}
