package com.management.admin.controller;

import com.management.admin.biz.ILinksService;
import com.management.admin.biz.impl.KhamwisServiceImpl;
import com.management.admin.biz.impl.OrdersServiceImpl;
import com.management.admin.biz.impl.ProductServiceImpl;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Khamwis;
import com.management.admin.entity.db.Links;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Product;
import com.management.admin.utils.StringUtil;
import com.management.admin.utils.web.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrdersController
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/20 13:20
 * Version 1.0
 **/

@Controller
@RequestMapping(value = "orders")
public class OrdersController {

    @Autowired
    private OrdersServiceImpl ordersService;
    @Autowired
    private ILinksService linksService;
    @Autowired
    private KhamwisServiceImpl khamwisService;
    @Autowired
    private ProductServiceImpl productService;


    /**
     * 跳转到订单列表 Timor 2019-2-20 14:15:54
     *
     * @return
     */
    @GetMapping(value = "orderlist")
    public String orderList() {
        return "orders/show";
    }

    /**
     * 订单详情页 弹出层 Timor 2019-2-21 10:00:49
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping(value = "lineitem")
    public String orderDetails(String orderId, Model model) {
        Orders order = ordersService.selectOrderById(orderId);
        Product product = productService.selectProductById(order.getPid());
        // 初始化信息
        model.addAttribute("khamwis", null);
        model.addAttribute("links", null);
        model.addAttribute("cardNumber", null);
        model.addAttribute("cardPwd", null);
        model.addAttribute("link", null);

        String linkId = order.getLinkId();

        //卡密
        if (product.getType() == 1) {
            if(StringUtils.isNotBlank(linkId)) {
                String[] split = linkId.split(",");
                List<Khamwis> khamwis = new ArrayList<>();
                for (String str : split) {
                    Khamwis khamwi = khamwisService.selectOneById(Integer.parseInt(str));
                    khamwis.add(khamwi);
                }
                if (khamwis.size() > 0) {
                    model.addAttribute("khamwis", khamwis);
                    model.addAttribute("links", null);
                }
            }
            //链接
        } else if (product.getType() == 2) {
            if (StringUtils.isNotBlank(linkId)) {
                String[] split = linkId.split(",");
                List<Links> links = new ArrayList<>();
                for (String str : split) {
                    Links link = linksService.selectOneById(Integer.parseInt(str));
                    links.add(link);
                }

                if (links != null) {
                    model.addAttribute("links", links);
                    model.addAttribute("khamwis", null);
                    model.addAttribute("cardNumber", null);
                    model.addAttribute("cardPwd", null);
                }
            }
        }
        model.addAttribute("order", order);
        return "orders/details";
    }

    /**
     * 编辑订单弹出层 Timor 2019-2-21 10:00:49
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping(value = "edit")
    public String orderEdit(String orderId, Model model) {
        Orders order = ordersService.selectOrderById(orderId);
        model.addAttribute("order", order);
        return "orders/compile";
    }

    /**
     * 分页加载所有订单 Timor 2019-2-20 14:13:34
     *
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "showOrders")
    @ResponseBody
    public JsonArrayResult<Orders> showAllOrders(Integer page, String limit, String condition, Integer status, String beginTime, String endTime) {

        Integer count = 0;
        List<Orders> ordersLimit = ordersService.getOrdersLimit(page, limit, condition, status, beginTime, endTime);
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, ordersLimit);
        count = ordersService.getLimitCount(condition, status, beginTime, endTime);
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }


    /**
     * 根据订单号修改订单信息
     *
     * @param orders
     * @return
     */
    @PostMapping(value = "editOrder")
    @ResponseBody
    public JsonResult<Orders> editOrders(Orders orders) {
        Integer result = ordersService.editOrderById(orders);
        if (result > 0) {
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

}
