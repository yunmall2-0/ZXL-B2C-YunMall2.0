package com.management.admin.entity.dbExt;

import com.management.admin.entity.db.Product;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {
    private String page;
    private String productType;
    private Integer typeId;
    private List<Product> productList;


}
