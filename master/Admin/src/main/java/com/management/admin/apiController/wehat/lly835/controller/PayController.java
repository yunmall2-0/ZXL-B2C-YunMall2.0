package com.management.admin.apiController.wehat.lly835.controller;

import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.lly835.bestpay.utils.JsonUtil;
import com.management.admin.apiController.wehat.lly835.service.WeChatPayService;
import com.management.admin.biz.IOrdersService;
import com.management.admin.biz.IUserService;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Users;
import com.management.admin.entity.dbExt.OrderDTO;
import com.management.admin.entity.template.SessionModel;
import com.management.admin.utils.http.UUID;
import com.management.admin.utils.web.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Random;

/**
 * 支付相关
 *
 * @version 1.0 2017/3/2
 * @auther Timor
 * @since 1.0
 */


@Controller
@Slf4j
@RequestMapping("api/wechatpay")
public class PayController {

    @Autowired
    private IUserService usersService;
    @Autowired
    private IOrdersService orderService;
    @Autowired
    private WeChatPayService payService;
    @Autowired
    private BestPayServiceImpl bestPayService;

    /**
     * 发起支付
     */


    @GetMapping("unifiedorder")
    public ModelAndView pay(HttpServletRequest req, String orderId, Map<String, Object> map) throws Exception {


        OrderDTO orderDTO = new OrderDTO();

        Orders orders = null;
        PayRequest request = new PayRequest();
        orders = orderService.selectOrderById(orderId);
        Random random = new Random();
/*        orderDTO.setOrderId(orders.getOrderId().toString());
        orderDTO.setOrderAmount(orders.getAmount());
        orderDTO.setPayName(orders.getPName());

        SessionModel sessionModel = SessionUtil.getSession(req);
        Users user = usersService.getUserById(orders.getUid());
        orderDTO.setBuyerOpenid(user.getOpenId());*/
        orderDTO.setBuyerOpenid("oFPHG1GftBSAiHC-g9Xl0VP5K_Ro");
        orderDTO.setOrderAmount(0.01);
        orderDTO.setPayName("测试");
        orderDTO.setOrderId(UUID.next()+"");
        //根据订单id查询订单,数据库查询
        //OrderDTO orderDTO = orderService.findOne(orderId);  //先不做处理
        if (orderDTO == null) { //订单不存在抛出错误
            throw new RuntimeException();
        }
        //支付请求参数
        PayResponse payResponse = payService.create(orderDTO);

        map.put("payResponse", payResponse);

        return new ModelAndView("wxpay/index", map);
    }


}
