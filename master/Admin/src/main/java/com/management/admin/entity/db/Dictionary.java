/***
 * @pName management
 * @name Dictionary
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.entity.db;

import lombok.*;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.data.annotation.TypeAlias;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dictionarys")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Dictionary {
    @Id
    private Integer dictionaryId;
    @Column(name = "`key`")
    private String key;
    @Column(name = "`value`")
    private String value;
}
