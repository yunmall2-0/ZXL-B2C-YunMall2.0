package com.management.admin.entity.dbExt;

import lombok.*;

/**
 * @ClassName ProductDetails
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/28 13:10
 * Version 1.0
 **/

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhLinks {

    /**
     * 商品类型
     */
    private Integer productType;
    /**
     * 卡号
     */
    private String cardNumber;
    /**
     * 密码
     */
    private String cardPwd;
    /**
     * 链接
     */
    private String link;

}
