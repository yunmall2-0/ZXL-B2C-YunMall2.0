package com.management.admin.biz.impl;

import com.alipay.api.domain.OrderDetail;
import com.management.admin.biz.IOrdersService;
import com.management.admin.entity.db.*;
import com.management.admin.entity.dbExt.KhLinks;
import com.management.admin.entity.dbExt.OrdersDatails;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.*;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.utils.DateUtil;
import com.management.admin.utils.IdWorker;
import com.management.admin.utils.web.SessionUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrdersServiceImpl
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/20 11:42
 * Version 1.0
 **/

@Service
public class OrdersServiceImpl implements IOrdersService {


    private final OrderMapper orderMapper;
    private final UsersMapper usersMapper;
    private final ProductMapper productMapper;
    private final LinksMapper linksMapper;
    private final KhamwisMapper khamwisMapper;

    @Autowired
    public OrdersServiceImpl(OrderMapper orderMapper, UsersMapper usersMapper, ProductMapper productMapper,
                             LinksMapper linksMapper, KhamwisMapper khamwisMapper) {
        this.orderMapper = orderMapper;
        this.usersMapper = usersMapper;
        this.productMapper = productMapper;
        this.khamwisMapper = khamwisMapper;
        this.linksMapper = linksMapper;
    }

    /**
     * 分页加载 Timor 2019-2-20 13:18:26
     *
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<Orders> getOrdersLimit(Integer page, String limit, String condition, Integer status, String beginTime, String endTime) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractLimitWhere(condition, status, beginTime, endTime);
        List<Orders> list = orderMapper.selectLimit(page, limit, beginTime, endTime, where);
        return list;
    }


    /**
     * 加载分页记录数 Timor 2019-2-20 13:19:08
     *
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer getLimitCount(String condition, Integer status, String beginTime, String endTime) {
        String where = extractLimitWhere(condition, status, beginTime, endTime);
        return orderMapper.selectLimitCount(beginTime, endTime, where);
    }

    /**
     * 查询用户分页订单 Timor  2019-2-26 14:16:50
     *
     * @param page
     * @param limit
     * @param condition
     * @param userId
     * @return
     */
    @Override
    public List<OrdersDatails> selectOrdersByPages(Integer page, String limit, String condition, Integer userId) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit);
        String where = extractPageWhere(condition);
        List<OrdersDatails> list = orderMapper.selectOrdersPage(limit, page, userId, where);
        return list;
    }

    @Override
    public Integer getPageCount(String condition, Integer userId) {
        String where = extractPageWhere(condition);
        return orderMapper.selectPageCount(where, userId);
    }

    /**
     * 总记录数 Timor 2019-2-20 13:19:32
     *
     * @return
     */
    @Override
    public Integer getCount() {
        return orderMapper.getCount();
    }

    /**
     * 提取分页条件
     *
     * @return
     */
    private String extractLimitWhere(String condition, Integer status, String beginTime, String endTime) {
        // 查询模糊条件
        String where = " 1=1";
        if (status != null) {
            where += " and status=" + status;
        }

        if (condition != null && condition.length() > 0) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("order_id", condition);
            if (condition.split("-").length == 2) {
                where += " OR " + ConditionUtil.like("add_time", condition);

            }
            where += " OR " + ConditionUtil.like("uname", condition);
            where += " OR " + ConditionUtil.like("amount", condition);
            where += " OR " + ConditionUtil.like("trade_type", condition) + ")";
        }
        // 取两个日期之间或查询指定日期
        where = extractBetweenTime(beginTime, endTime, where);
        return where.trim();
    }

    private String extractPageWhere(String condition) {
        // 查询模糊条件
        String where = " 1=1 ";
        if (condition != null && condition.length() > 0) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("order_id", condition, true, "t1");
            where += " OR " + ConditionUtil.like("pname", condition, true, "t1") + ")";
        }
        // 取两个日期之间或查询指定日期
        where = extractBetweenTime("", "", where);
        return where.trim();
    }


    /**
     * 提取两个日期之间的条件
     *
     * @return
     */
    private String extractBetweenTime(String beginTime, String endTime, String where) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        } else if (beginTime != null && beginTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        } else if (endTime != null && endTime.contains("-")) {
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }

    /**
     * 根据订单Id 查询相应的订单信息 Timor  2019-2-21 09:59:09
     *
     * @param orderId
     * @return
     */
    @Override
    public Orders selectOrderById(String orderId) {
        return orderMapper.selectOrder(orderId);
    }

    /**
     * 根据订单Id 修改订单信息
     *
     * @param orders
     * @return
     */
    @Override
    public Integer editOrderById(Orders orders) {
        return orderMapper.updateOrderId(orders);
    }


    /**
     * 有用户信息生成订单
     *
     * @param userId
     * @param productId
     * @param tradeType
     * @return
     */
    @Override
    @Transactional
    public Orders createOrder(Integer userId, Long productId, Integer tradeType, String phone, Integer number) throws Exception {
        Orders orders = new Orders();
        //查询用户信息
        Users users = usersMapper.slectUserById(userId);
        //查询商品
        Product product = productMapper.selectById(productId);

        Orders publicCreateOrders = publicCreateOrders(orders, users, product, tradeType, phone, number, 0, users.getContactWay(), null);
        return publicCreateOrders;

    }

    /**
     * 无用户信息(游客)生成订单
     *
     * @param contactWay
     * @param productId
     * @param tradeType
     * @return
     */
    @Override
    @Transactional
    public Orders createOrder(String contactWay, Long productId, Integer tradeType, String phone, Integer number) throws Exception {
        Orders orders = new Orders();
        //查询商品
        Product product = productMapper.selectById(productId);
        //查询商家信息
        Users users = usersMapper.slectUserById(product.getShopId());
        Orders publicCreateOrders = publicCreateOrders(orders, users, product, tradeType, phone, number, 0, contactWay, null);
        return publicCreateOrders;
    }


    /**
     * 公共的生成订单方法 钉子 2019年3月2日14:59:14
     *
     * @param orders
     * @param users
     * @param product
     * @param
     * @return
     */
    private Orders publicCreateOrders(Orders orders, Users users, Product product, Integer tradeType, String phone, Integer number, Integer status, String contactWay, String parentId) throws Exception {
        Users shopUser = usersMapper.slectUserById(product.getShopId());
        Long olderId = IdWorker.getFlowIdWorkerInstance().nextId();
        orders.setOrderId(olderId.toString());
        if (StringUtils.isNotBlank(parentId)) {
            orders.setParentId(parentId);
        } else {
            orders.setParentId(String.valueOf(olderId));
        }
        orders.setPid(product.getProductId());
        orders.setPName(product.getProductName());
        orders.setSid(shopUser.getUserId());
        orders.setSName(shopUser.getUserName());
        if (status == 0) {
            orders.setUid(users.getUserId());
            orders.setUName(users.getUserName());
        } else {
            orders.setUid(2);
            orders.setUName("游客");
        }
        orders.setTradeType(tradeType);
        orders.setPrice(product.getAmount());
        orders.setNumber(number);
        orders.setAmount(product.getAmount() * number);
        orders.setContact(contactWay);
        orders.setStatus(0);//待支付
        if (phone != null && phone.length() != 0) {
            orders.setPhone(phone);//直充方式时，手机号
        }
        Integer integer = orderMapper.insertOrder(orders);
        if (integer <= 0) {
            return null;
        } else {
            return orders;
        }
    }

    /**
     * 订单改为已发货(绑定流水号，链接Id或者卡密ID)
     *
     * @param payId
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public Integer olderDelivered(String payId, String orderId) {
        // 查询订单信息
        Orders orders = orderMapper.selectOrder(orderId);
        // 查询订单相应商品信息
        Product product = productMapper.selectById(orders.getPid());
        int result = 0;
        if (StringUtils.isNotBlank(orders.getParentId())) {
            return publicUpdateOrder(orders, product, payId);
        } else {
            Orders order = orderMapper.selectOrder(orderId);
            result = publicUpdateOrder(order, product, payId);

        }
        return result;
    }

    /**
     * 修改订单公共方法 钉子 2019年3月2日16:29:50
     *
     * @param orders
     * @param product
     * @param payId
     * @return
     */
    private Integer publicUpdateOrder(Orders orders, Product product, String payId) {
        // 修改订单为已发货
        Integer integer = orderMapper.updateOrderStatus(2, orders.getOrderId());
        if (integer <= 0) throw new MsgException("订单状态修改失败！");
        //判断商品类型是卡密还是链接
        if (product.getType() == 1) {
            // 如果是卡密
            // 获取商品对应的卡密库存
            List<Khamwis> khamwisList = khamwisMapper.selectKhamwiByProductId(orders.getPid());
            // 获取第一条卡密信息
            String khamwi = "";
            for (int i = 0; i < orders.getNumber(); i++) {
                khamwi += khamwisList.get(i).getCardId() + ",";
                // 修改为已使用
                khamwisMapper.updateKhamwis(khamwisList.get(i).getCardId());
            }
            // 修改订单流水号
            Integer pay = orderMapper.updateOrderPayId(payId, orders.getOrderId());
            int index = khamwi.lastIndexOf(","); //去掉最后一个','号
            khamwi = khamwi.substring(0, index) + khamwi.substring(index + 1, khamwi.length());
            // 订单绑定卡密
            Integer link = orderMapper.updateLinkId(khamwi, orders.getOrderId());
            if (pay > 0 && link > 0) {
                return 1;
            }
        } else if (product.getType() == 2) {
            List<Links> linksList = linksMapper.selectLinksByProductId(orders.getPid());
            String links = "";
            for (int i = 0; i < orders.getNumber(); i++) {
                // 拼接链接字符
                links += linksList.get(i).getLinkId() + ",";
                // 修改链接状态
                linksMapper.updateLinks(linksList.get(i).getLinkId());
            }
            Integer pay = orderMapper.updateOrderPayId(payId, orders.getOrderId());
            int index = links.lastIndexOf(","); //去掉最后一个','号
            links = links.substring(0, index) + links.substring(index + 1, links.length());

            Integer link = orderMapper.updateLinkId(links, orders.getOrderId());
            if (pay > 0 && link > 0) {
                return 1;
            }
        } else {
            // 直冲
            Integer pay = orderMapper.updateOrderPayId(payId, orders.getOrderId());
            // 直冲类型只为已付款
            Integer result = orderMapper.updateOrderStatus(1, orders.getOrderId());
            if (pay > 0 && result > 0) return 1;
        }
        return -1;
    }


    /**
     * 查询订单详情  Timor   2019-2-28 11:58:07
     *
     * @param orderId
     * @return
     */
    @Override
    public List<KhLinks> queryDetails(String orderId) {
        Orders orders = orderMapper.selectOrder(orderId);
        Product product = productMapper.selectById(orders.getPid());

        List<KhLinks> list = new ArrayList<>();
        //类型为直充类型
        if (product.getType() == 0) {
            return null;
            //卡密类型
        } else if (product.getType() == 1) {
            String linkId = orders.getLinkId();
            String[] split = linkId.split(",");
            for (String str : split) {
                KhLinks khLinks = new KhLinks();
                Khamwis khamwis = khamwisMapper.selectOneById(Integer.parseInt(str));
                khLinks.setCardNumber(khamwis.getCardNumber());
                khLinks.setCardPwd(khamwis.getCardPwd());
                list.add(khLinks);
            }


            //链接
        } else if (product.getType() == 2) {
            String linkId = orders.getLinkId();
            String[] split = linkId.split(",");
            for (String str : split) {
                KhLinks khLinks = new KhLinks();
                Links links = linksMapper.selectOneById(Integer.parseInt(str));
                khLinks.setLink(links.getLink());
                list.add(khLinks);
            }


        }
        return list;
    }

    /**
     * 检验库存  钉子 2019年3月2日15:52:19
     *
     * @param productId
     * @param number
     * @return
     */
    @Override
    public Integer checkInventory(String productId, Integer number) {
        Product product = productMapper.selectById(Long.valueOf(productId));
        if (product.getType() == 1) {
            List<Khamwis> khamwisList = khamwisMapper.selectKhamwiByProductId(Long.valueOf(productId));
            if (khamwisList.size() >= number) {
                return 1;
            } else {
                return 0;
            }
        } else if (product.getType() == 2) {
            List<Links> linksList = linksMapper.selectLinksByProductId(Long.valueOf(productId));
            if (linksList.size() >= number) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 1;
        }
    }

    /**
     * 根据订单号查询一个未付款的订单（包含商品信息） 狗蛋 2019年2月28日11:35:30
     *
     * @param orderId
     * @return
     */
    @Override
    public OrdersDatails getOrderById(String orderId) {
        if (StringUtils.isBlank(orderId)) throw new MsgException("订单号不能为空！");
        OrdersDatails ordersDatails = orderMapper.selectOrderById(orderId);
        Integer linkId = ordersDatails.getLinkId();
        if (linkId != null) {
            if (ordersDatails.getType().equals(1)) {
                Khamwis khamwis = khamwisMapper.selectOneById(linkId);
                ordersDatails.setCardNumber(khamwis.getCardNumber());
                ordersDatails.setCardPwd(khamwis.getCardPwd());
            }
            if (ordersDatails.getType().equals(2)) {
                Links links = linksMapper.selectOneById(linkId);
                ordersDatails.setLink(links.getLink());
            }
        }
        switch (ordersDatails.getTradeType()) {
            case 0:
                ordersDatails.setTradeTypeStr("站内");
                break;
            case 1:
                ordersDatails.setTradeTypeStr("支付宝");
                break;
            case 2:
                ordersDatails.setTradeTypeStr("微信");
                break;
        }
        ordersDatails.setAddTimeStr(DateUtil.getFormatDateTime(ordersDatails.getAddTime(), "yyyy-MM-dd HH:mm:ss"));
        ordersDatails.setEditTimeStr(DateUtil.getFormatDateTime(ordersDatails.getEditTime(), "yyyy-MM-dd HH:mm:ss"));
        return ordersDatails;
    }


    /**
     * 根据订单状态查询用户该状态的所有订单信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> getOrderByStatus(Integer status, Integer userId) {
        return orderMapper.selectOrderByStatus(status, userId);
    }

    /**
     * 获取用户的所有订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<OrdersDatails> getOrderByUserId(Integer userId) {
        List<OrdersDatails> ordersDatails = orderMapper.selectOrderByUserId(userId);

        ordersDatails.forEach(item -> {
            switch (item.getStatus()) {
                case 0:
                    item.setStatusStr("待支付");
                    break;
                case 1:
                    item.setStatusStr("待发货");
                    break;
                case 2:
                    item.setStatusStr("已发货");
                    break;

                case 3:
                    item.setStatusStr("已收货");
                    break;
                case 4:
                    item.setStatusStr("已关闭");
                    break;
                case 5:
                    item.setStatusStr("异常关闭");
                    break;
            }
        });

        return ordersDatails;
    }
}
