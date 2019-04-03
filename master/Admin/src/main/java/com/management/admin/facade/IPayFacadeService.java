package com.management.admin.facade;

import com.management.admin.entity.db.Orders;

public interface IPayFacadeService {

    /**
     * 生成订单并付款 狗蛋 2019年2月28日10:34:24
     * @param productId
     * @param phone
     * @param type
     * @param userId
     * @return
     */
    Orders pay(String productId, String phone, Integer type,
                Integer userId,Integer number) throws Exception;

    /**
     * 支付为支付订单 狗蛋 2019年2月28日10:35:37
     * @param orderId
     * @param userId
     * @return
     */
    Orders payment(String orderId,Integer userId);

    Orders dock(Integer userId,Orders order);
}
