package com.management.admin.apiController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.OrderDetail;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import com.management.admin.biz.IUserService;
import com.management.admin.biz.impl.OrdersServiceImpl;
import com.management.admin.entity.Constant;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.dbExt.KhLinks;
import com.management.admin.entity.dbExt.OrdersDatails;
import com.management.admin.entity.resp.WxOrder;
import com.management.admin.entity.template.SessionModel;
import com.management.admin.exception.MsgException;
import com.management.admin.facade.IPayFacadeService;
import com.management.admin.utils.DateUtil;
import com.management.admin.utils.TokenUtil;
import com.management.admin.utils.web.CookieUtil;
import com.management.admin.utils.web.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OlderApiController
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/25 14:27
 * Version 1.0
 **/

@Controller
@Slf4j
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IPayFacadeService payFacadeService;

    @Autowired
    private BestPayService bestPayService;


    /**
     * 根据订单号查询订单 Timor  2019-2-25 15:30:50
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "queryOrder")
    @ResponseBody
    public JsonResult queryOrdersById(String orderId) {
        Orders orders = ordersService.selectOrderById(orderId);
        if (orders != null) {
            return new JsonArrayResult().successful(orders);
        }
        return JsonResult.failing();
    }


    /**
     * 根据用户Id查询订单(包含模糊查询) Timor  2019-2-25 15:31:35
     *
     * @param request
     * @param limit
     * @param page
     * @return
     */
    @GetMapping(value = "showOrders")
    @ResponseBody
    public JsonResult queryOrdersByUid(HttpServletRequest request, String limit, Integer page, String condition) {
        Integer userId = SessionUtil.getSession(request).getUserId();
        Integer count = 0;
        List<OrdersDatails> ordersLimit = ordersService.selectOrdersByPages(page, limit, condition, userId);
        ordersLimit.forEach(item -> {
            item.setAddTimeStr(DateUtil.getFormatDateTime(item.getAddTime(), "yyyy-MM-dd HH:mm:ss"));
        });
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, ordersLimit);
        count = ordersService.getPageCount(condition, userId);
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 根据订单号查询订单详情(卡密或是连接) Timor  2019-2-28 13:04:57
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "queryDeatails")
    @ResponseBody
    public JsonResult queryOrderDetails(String orderId) {
        List<KhLinks> details = ordersService.queryDetails(orderId);
        return new JsonArrayResult().successful(details);
    }

    /**
     * 检验商品库存 钉子 2019年3月2日16:04:11
     *
     * @param productId
     * @param number
     * @return
     */
    @RequestMapping(value = "checkProductInventory")
    @ResponseBody
    public JsonResult checkProductInventory(String productId, Integer number) {
        Integer integer = ordersService.checkInventory(productId, number);
        if (integer == 1) {
            return JsonResult.successful();
        } else {
            throw new MsgException("商品库存不足");
        }
    }

    /**
     * 用户下单 狗蛋 2019年2月28日10:55:28
     *
     * @param productId  商品编号
     * @param phone      直冲到账手机号
     * @param type       交易类型 0：钱包，1：支付宝，微信
     * @param identity   身份 1：用户，0：游客
     * @param contactWay 游客联系方式
     * @param request
     * @param response
     * @return
     */
    @PostMapping("placeAnOrder")
    public JsonResult placeAnOrder(String productId, String phone, Integer type,
                                   Integer identity, String contactWay, String payPassword, Integer number,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token
        Orders order = null;
        if (identity.equals(1)) {
            // TODO 写死
            Integer userId = 1;
//            Integer userId = SessionUtil.getSession(request).getUserId();
            if (type.equals(0)) {
                // 验证支付密码
                Boolean verifyPayPassword = userService.verifyPayPassword(userId, payPassword);
                if (!verifyPayPassword) throw new MsgException("支付密码不正确！");
                order = payFacadeService.pay(productId, phone, type, userId, number);
            } else if (type.equals(2)) {
                // TODO 重定向发起微信支付
            } else {
                order = ordersService.createOrder(userId, Long.parseLong(productId), 1, phone, number);
                // TODO 重定向发起支付宝支付

            }
        } else {
            // 游客身份
            order = ordersService.createOrder(contactWay, Long.parseLong(productId), 1, phone, number);
            // TODO 重定向发起支付宝支付
        }
        if (order == null) throw new MsgException("订单信息生成失败！");
        return new JsonResult(200, order);
    }


    /**
     * 用户下单 狗蛋 2019年2月28日10:55:28
     *
     * @param productId 商品编号
     * @param phone     直冲到账手机号
     * @param type      交易类型 0：钱包，1：支付宝，微信
     * @param identity  身份 1：用户，0：游客
     * @param request
     * @param response
     * @return
     */
    @PostMapping("placeWxAnOrder")
    public String placeWxAnOrder(@RequestParam("productId") String productId,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("type") Integer type,
                                 @RequestParam("identity") Integer identity,
                                 @RequestParam("number") Integer number,
                                 @RequestParam("userId") String userId,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token
        String token = (String) request.getSession().getAttribute("token");

        log.debug("获取到的信息为："+token);

        Integer userids = 0; // 代表最后的用户编号值
        if(userId.length()>15){
            Map<String, String> validate = TokenUtil.validate(userId);
            userids = Integer.parseInt(validate.get("userId"));
        }else{
            userids = Integer.parseInt(userId);
        }

        Orders order = null;
        if (identity.equals(1)) {

/*            Map<String, String> validate = TokenUtil.validate(userId);
            String userId1 = validate.get("userId");*/

/*            // TODO 写死
            Integer userId = 1;*/
//
            if (type.equals(2)) {
                order = ordersService.createOrder(userids, Long.parseLong(productId), 2, phone, number);

                return "redirect:" + Constant.CALLBACK + "/api/wechatpay/unifiedorder?orderId=" + order.getOrderId();
            } else {

                order = ordersService.createOrder(userId, Long.parseLong(productId), 1, phone, number);
                // TODO 重定向发起支付宝支付
            }
        }
        if (order == null) throw new MsgException("订单信息生成失败！");
        return "";
    }

    /**
     * 微信支付完成回调
     *
     * @param request
     * @param notifyData
     * @return
     */
    @RequestMapping("paymentSuccess")
    public ModelAndView paymentSuccess(HttpServletRequest request, @RequestBody String notifyData) {
        PayResponse response = bestPayService.asyncNotify(notifyData);
        WxOrder wxOrder = com.management.admin.utils.JsonUtil.getModel(JsonUtil.toJson(response), WxOrder.class);
        Orders orders = null;
        if (wxOrder.getOrderId() != null && wxOrder.getOrderId().length() > 0) {
            orders = ordersService.selectOrderById(wxOrder.getOrderId());

            if (orders.getStatus().equals(1)) {
                return new ModelAndView("wxpay/success");
            }
            Orders dock = payFacadeService.dock(orders.getUid(), orders);

            return new ModelAndView("wxpay/success");


        }
        return new ModelAndView("errPage/index");
    }


    /**
     * 去支付 狗蛋 2019年2月28日10:39:55
     *
     * @param orderId
     * @param request
     * @param response
     * @return
     * @Param type 交易类型 0钱包 1支付宝
     */
    @PostMapping("payment")
    public JsonResult payment(String orderId, Integer type, String payPassword,
                              HttpServletRequest request, HttpServletResponse response) {
        if (type.equals(0)) {

            Integer userId = SessionUtil.getSession(request).getUserId();
            if (StringUtils.isBlank(orderId)) throw new MsgException("订单编号为空！");
            // 验证支付密码
            Boolean verifyPayPassword = userService.verifyPayPassword(userId, payPassword);
            if (!verifyPayPassword) throw new MsgException("支付密码不正确！");
            Orders payment = payFacadeService.payment(orderId, userId);
            return new JsonResult(200, payment);
        }
        // TODO 发起支付
        return new JsonResult(200, ordersService.selectOrderById("618564707690745856"));
    }

    /**
     * 订单去支付（微信支付）
     * @param orderId
     * @param userId
     * @return
     */
    @PostMapping("wechatPayment")
    public String wechatPayment(String orderId,String userId){
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token
        Integer userids = 0; // 代表最后的用户编号值


        if(userId.length()>15){
            Map<String, String> validate = TokenUtil.validate(userId);
            userids = Integer.parseInt(validate.get("userId"));
        }else{
            userids = Integer.parseInt(userId);
        }

        if (StringUtils.isBlank(orderId)) throw new MsgException("订单编号为空！");
        Orders order = ordersService.selectOrderById(orderId);
        return "redirect:" + Constant.CALLBACK + "/api/wechatpay/unifiedorder?orderId=" + order.getOrderId();
    }

    /**
     * 获取订单详情 根据订单编号（包括商品信息）
     *
     * @param orderId
     * @return
     */
    @GetMapping("getOrderById")
    @ResponseBody
    public JsonResult getOrderById(String orderId) {
        // 查询订单
        OrdersDatails order = ordersService.getOrderById(orderId);

        if (order == null) throw new MsgException("订单信息获取失败！");


        return new JsonResult(200, order);
    }

    /**
     * 获取订单详情（包括连接卡密信息）
     * @param orderId
     * @return
     */
    @GetMapping("getOrderDetailsById")
    @ResponseBody
    public JsonResult getOrderDetailsById(String orderId){
        // 查询订单
        OrdersDatails order = ordersService.getOrderById(orderId);

        if (order == null) throw new MsgException("订单信息获取失败！");
        List<KhLinks> khLinks = ordersService.queryDetails(order.getOrderId());

        if(khLinks!=null&&khLinks.size()>0){
            order.setKhLinks(khLinks);
        }
        return new JsonResult(200,order);
    }

    /**
     * 第三方交易完成，检查订单状态 狗蛋 2019年2月28日14:26:25
     *
     * @param orderId
     * @return
     */
    @GetMapping("check")
    @ResponseBody
    public JsonResult check(String orderId) {
        Orders order = ordersService.selectOrderById(orderId);
        if (order != null && order.getStatus() > 0) return JsonResult.successful();
        return JsonResult.failing();
    }

    /**
     * 根据订单状态查询用户的订单列表
     *
     * @param status
     * @param request
     * @param response
     * @return
     */
    @GetMapping("getOrderByStatus")
    @ResponseBody
    public JsonResult getOrderByStatus(Integer status,String userId,HttpServletRequest request, HttpServletResponse response) {
        if (status == null) throw new MsgException("订单状态为空");
       /* Integer userId = SessionUtil.getSession(request).getUserId();*/
        Integer userids = 0; // 代表最后的用户编号值
        if(userId.length()>15){
            Map<String, String> validate = TokenUtil.validate(userId);
            userids = Integer.parseInt(validate.get("userId"));
        }else{
            userids = Integer.parseInt(userId);
        }
        List<Orders> ordersList = ordersService.getOrderByStatus(status, userids);
        if (ordersList.size() <= 0) throw new MsgException("没有该状态的订单");
        return new JsonResult(200, ordersList);

    }


    /**
     * 获取用户的所有订单
     * @return
     */
    @PostMapping("getOrderByUserId")
    @ResponseBody
    public JsonResult getOrderByUserId(String userId){
        Integer userids = 0; // 代表最后的用户编号值
        if(userId.length()>15){
            Map<String, String> validate = TokenUtil.validate(userId);
            userids = Integer.parseInt(validate.get("userId"));
        }else{
            userids = Integer.parseInt(userId);
        }
        List<OrdersDatails> orderByUserId = ordersService.getOrderByUserId(userids);
        return new JsonResult(200,orderByUserId);
    }



    @GetMapping("test")
    public void test(HttpServletResponse response,HttpServletRequest request){
        response.setHeader("Content-disposition","attachment;filename = "+"test");
        response.setContentType("text/plain"); //text/plain 就会导出成txt了
    }

}
