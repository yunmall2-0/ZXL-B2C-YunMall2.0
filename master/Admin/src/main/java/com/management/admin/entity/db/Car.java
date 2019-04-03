package com.management.admin.entity.db;

import io.swagger.models.auth.In;
import lombok.*;

import javax.persistence.Table;
import java.util.List;

@Table(name = "car")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    private Integer carId;
    private Integer userId;
    private Long productId;
    private Boolean isDelete;
    private List<Product> productList;
}
