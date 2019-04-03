/***
 * @pName management
 * @name Exception
 * @user HongWei
 * @date 2018/8/13
 * @desc
 */
package com.management.admin.entity.db;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

import java.util.Date;

@Table(name = "exceptions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Exceptions {
    @Id
    private Integer logId;
    private String body;
    private Date addDate;
}
