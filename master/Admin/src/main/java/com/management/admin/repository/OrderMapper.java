package com.management.admin.repository;

import com.alipay.api.domain.OrderDetail;
import com.management.admin.entity.db.Accounts;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.dbExt.OrdersDatails;
import org.apache.ibatis.annotations.*;
import org.omg.CORBA.INTERNAL;
import org.simpleframework.xml.Order;

import java.util.List;

/**
 * 订单相关接口
 */

@Mapper
public interface OrderMapper extends MyMapper<Orders> {

    /**
     * 查询所有的订单 Timor 2019年2月20日10:32:47
     *
     */
    @Select("select * from orders where parent_id is not NULL")
    List<Orders> showOrders();


    /**
     * 查询分页 Timor 2019年2月20日10:32:47
     * @param page
     * @param limit
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("SELECT * from orders WHERE parent_id is not NULL and ${condition} ORDER BY add_time DESC LIMIT #{page},${limit}")
    List<Orders> selectLimit(@Param("page") Integer page, @Param("limit") String limit
            , @Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);



    /**
     * 分页查询用户订单 Timor 2019-2-25 14:56:09
     * @param limit
     * @param page
     * @param userId
     * @param condition
     * @return
     */
    @Select("select *  from orders t1 left join  products t2 on  t1.pid=t2.product_id  where parent_id is not NULL and ${condition} and uid=#{userId} ORDER BY t1.add_time DESC LIMIT #{page},${limit}")
    List<OrdersDatails> selectOrdersPage(@Param("limit") String limit, @Param("page") Integer page,
                                         @Param("userId") Integer userId, @Param("condition") String condition);


    /**
     * 查询分页记录总数 Timor 2019年2月20日10:32:47
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select count(order_id) from orders  where parent_id is not NULL and ${condition}")
    Integer selectLimitCount(@Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);


    /**
     * 查询分页记录总数 Timor 2019年2月20日10:32:47
     * @param userId
     * @param condition
     * @return
     */
    @Select("select count(order_id) from orders t1 left join products" +
            " t2 on t1.pid = t2.product_id where ${condition} and uid=#{userId} and parent_id is not NULL")
    Integer selectPageCount(@Param("condition") String condition,@Param("userId") Integer userId);


    /**
     * 查询总记录数 Timor 2019年2月20日10:32:47
     * @return
     */
    @Select("select count(order_id) from orders where parent_id is not NULL")
    Integer getCount();


    /**
     * 根据订单号查询订单 Timor 2019-2-20 11:04:28
     * @param orderId
     * @return
     */
    @Select("select * from orders where order_id=#{orderId}")
    Orders selectOrder(String  orderId);

    /**
     * 添加一条订单信息  Timor 2019年2月20日10:58:48
     * @param orders
     * @return
     */
    @Insert("insert into orders values (#{orderId},#{parentId},#{pid},#{pName},#{sid},#{sName},#{uid},#{uName}," +
            "#{tradeType},#{price},#{number},#{amount},#{payId},NOW(),NOW()," +
            "#{remark},#{status},#{phone},#{linkId},#{contact})")
    Integer insertOrder(Orders orders);

    /**
     *根据订单ID修改订单信息 Timor 2019-2-20 11:13:56
     * @param orders
     * @return
     */
    @Update("update orders set uname=#{uName}," +
            "edit_time=NOW()," +
            "status=#{status},phone=#{phone} where order_id=#{orderId}")
    Integer updateOrderId(Orders orders);


    /**
     * 根据订单号修改订单状态 Timor 2019-2-20 11:17:39
     * @param status
     * @return
     */
    @Update("update orders set status=#{status},edit_time=NOW() where order_id=#{orderId}")
    Integer updateOrderStatus(@Param("status") Integer status,@Param("orderId")String orderId);

    /**
     * 根据订单号修改订单流水号 Timor 2019-2-20 11:17:39
     * @param payId
     * @return
     */
    @Update("update orders set pay_id=#{payId},edit_time=NOW() where order_id=#{orderId}")
    Integer updateOrderPayId(@Param("payId") String  payId,@Param("orderId")String orderId);

    /**
     * 根据订单Id绑定链接ID 或 卡密Id Timor  2019-2-25 14:56:25
     * @param linkId
     * @param orderId
     * @return
     */
    @Update("update orders set link_id=#{linkId},edit_time=NOW() where order_id=#{orderId}")
    Integer updateLinkId(@Param("linkId") String linkId,@Param("orderId")String  orderId);


    /**
     * 查询用户订单信息 Timor 2019-2-25 14:56:15
     * @param userId
     * @return
     */
    @Select("select * from orders where uid=#{userId} ORDER BY add_time DESC ")
    List<Orders> selectOrdersByUserId(Integer userId);

    /**
     * 根据订单号查询一个未付款的订单（包含商品信息） 狗蛋 2019年2月28日11:35:30
     * @param orderId
     * @return
     */
    @Select("select * from orders t1 left join products t2" +
            " on t1.pid = t2.product_id where t1.order_id = #{orderId}")
    OrdersDatails selectOrderById(String orderId);

    /**
     * 根据父订单查询所有子订单 钉子 2019年3月2日16:26:12
     * @param parentId
     * @return
     */
    @Select("select * from orders where parent_id=#{parentId}")
    List<Orders> selectOrdersByParent(String parentId);

    /**
     * 根据订单状态查询用户订单信息
     * @param status
     * @return
     */
    @Select("select * from orders where status = #{status} and uid = #{userId}")
    List<Orders> selectOrderByStatus(@Param("status") Integer status,@Param("userId") Integer userId);

    /**
     * 查询用户的所有订单
     * @param userId
     * @return
     */
    @Select("select * from orders t1 left join products t2 on t1.pid=t2.product_id where uid = #{userId}")
    List<OrdersDatails> selectOrderByUserId(Integer userId);


}
