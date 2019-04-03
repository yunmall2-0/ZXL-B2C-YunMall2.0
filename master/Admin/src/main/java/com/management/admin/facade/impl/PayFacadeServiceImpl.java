package com.management.admin.facade.impl;

import com.management.admin.biz.IOrdersService;
import com.management.admin.biz.IPayService;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Pays;
import com.management.admin.exception.MsgException;
import com.management.admin.facade.IPayFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

@Service
public class PayFacadeServiceImpl implements IPayFacadeService {

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IPayService payService;

    /**
     * 生成订单并付款 狗蛋 2019年2月28日10:34:24
     * @param productId
     * @param phone
     * @param type
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Orders pay(String productId,String phone,Integer type,
                       Integer userId,Integer number) throws Exception{
        Orders order = null;
        order = ordersService.createOrder(userId, Long.parseLong(productId), 0,phone,number);
        return dock(userId,order);
    }

    /**
     * 支付为支付订单 狗蛋 2019年2月28日10:35:37
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public Orders payment(String orderId,Integer userId) {
        Orders order = ordersService.selectOrderById(orderId);
        return dock(userId,order);
    }

    /**
     * 订单支付通用方法 狗蛋 2019年2月28日10:34:30
     * @param userId
     * @param order
     * @return
     */
    public Orders dock(Integer userId,Orders order){
        if(order == null) throw new MsgException("订单生成失败！");
        // 扣余额生成流水
        Pays pays = payService.getMoney(userId, order.getAmount(),0);
        if(pays==null) throw new MsgException("流水生成失败！");
        // 订单回调，修改为已付款
        Integer integer = ordersService.olderDelivered(pays.getPayId().toString(), order.getOrderId());
        if(integer<=0) throw new MsgException("订单信息修改失败！");
        return order;
    }
}
