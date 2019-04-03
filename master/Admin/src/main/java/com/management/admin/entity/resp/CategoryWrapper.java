package com.management.admin.entity.resp;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWrapper {
    /**
     * 包装的类目ID
     */
    private Integer id;
    /**
     * 包装的类目名
     */
    private String title;

    /**
     * 包装的上级类目ID
     */
    private Integer pid;

    /**
     * 包装类排序
     */
    private Integer sort;

    /**
     * 是否为叶子级
     */
    private boolean isLeaf;


}
