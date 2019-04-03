package com.management.admin.biz;

import com.alipay.api.domain.OrderDetail;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.dbExt.KhLinks;
import com.management.admin.entity.dbExt.OrdersDatails;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.INTERNAL;
import org.simpleframework.xml.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IOrdersService {

    /**
     * 查询所有订单 Timor 2019-2-20 11:30:31
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    List<Orders> getOrdersLimit(Integer page, String limit, String condition,Integer  status ,String beginTime, String endTime);

    /**
     * 分页查询用户订单 Timor 2019-2-25 14:56:01
     * @param page
     * @param limit
     * @param condition
     * @param userId
     * @return
     */
    List<OrdersDatails> selectOrdersByPages(Integer page, String limit, String condition, Integer  userId);



    /**
     * 分页记录数 Timor 2019-2-20 11:30:57
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer getLimitCount(String condition, Integer  status ,String beginTime, String endTime);


    /**
     * 分页用户订单记录数 Timor 2019-2-26 14:30:00
     * @param condition
     * @param userId
     * @return
     */
    Integer getPageCount(String condition, Integer userId);



    /**
     * 查询所有记录数 Timor 2019-2-20 11:32:08
     * @return
     */
    Integer getCount();

    /**
     * 根据订单号查询相应的订单 Timor 2019-2-21 09:58:12
     * @param orderId
     * @return
     */
    Orders selectOrderById(String  orderId);


    /**
     * 根据订单ID修改订单信息 Timor 2019-2-21 15:25:59
     * @param orders
     * @return
     */
    Integer editOrderById(Orders orders);


    /**
     * 生成订单(针对于有用户信息)
     * @param userId
     * @param productId
     * @param tradeType
     * @return
     */
    Orders  createOrder(Integer userId, Long productId,Integer tradeType,String phone,Integer number) throws Exception;


    /**
     * 生成订单(针对于游客用户，无用户信息)
     * @param contactWay
     * @param productId
     * @param tradeType
     * @return
     */
    Orders createOrder(String contactWay,Long productId,Integer tradeType,String phone,Integer number) throws Exception;


    /**
     * 订单改为已发货(绑定流水号，链接Id或者卡密ID)
     * @param payId
     * @param orderId
     * @return
     */
    Integer olderDelivered(String payId,String orderId);

    /**
     * 根据订单号查询一个未付款的订单（包含商品信息） 狗蛋 2019年2月28日11:35:30
     * @param orderId
     * @return
     */
    OrdersDatails getOrderById(String orderId);

    /**
     * 查询订单详情 Timor  2019-2-28 11:57:22
     * @param orderId
     * @return
     */
    List<KhLinks> queryDetails(String orderId);

    /**
     * 检验库存  钉子 2019年3月2日15:51:29
     * @param productId
     * @param number
     * @return
     */
    Integer checkInventory(String productId,Integer number);

    /**
     * 根据订单状态查询用户该状态的所有订单信息
     * @param status
     * @param userId
     * @return
     */
    List<Orders> getOrderByStatus(Integer status,Integer userId);

    /**
     * 获取用户的所有订单
     * @param userId
     * @return
     */
    List<OrdersDatails> getOrderByUserId(Integer userId);

}
