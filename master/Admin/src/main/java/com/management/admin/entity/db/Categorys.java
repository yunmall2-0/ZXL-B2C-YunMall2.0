package com.management.admin.entity.db;

import lombok.*;

import javax.persistence.Table;
import java.util.List;

@Table(name = "categorys")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Categorys {
    private Integer categoryId;
    private Integer parentId;
    private String categoryName;
    private Boolean isLeaf;
    private Boolean isDelete;
    private Integer sort;

    private List<Categorys> categorys;
}
