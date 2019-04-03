package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeWrappage {

    private Integer categoryId;
    private Integer parentId;
    private String categoryName;
    private Boolean isLeaf;
    private Boolean isDelete;
    private Integer sort;

    private List<TreeWrappage> categoryList;
}
