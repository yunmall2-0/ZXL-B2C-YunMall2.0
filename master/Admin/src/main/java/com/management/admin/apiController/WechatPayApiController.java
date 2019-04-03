package com.management.admin.apiController;

import com.alibaba.fastjson.JSON;
import com.management.admin.biz.IOrdersService;
import com.management.admin.biz.IProductService;
import com.management.admin.biz.IUserService;
import com.management.admin.entity.Constant;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Users;
import com.management.admin.utils.HttpUtils;
import com.management.admin.utils.WechatUtil;
import com.management.admin.utils.http.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Controller
@RequestMapping("api/wechatpay")
public class WechatPayApiController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOrdersService ordersService;


    public ModelAndView unifiedorder(Long orderId, HttpServletRequest request, HttpServletResponse response) throws Exception {

//        Orders orders = ordersService.selectOrderById(orderId.toString());

//        Users user = userService.getUserById(orders.getUid());

        Map<String, Object> resultMap = new HashMap<>();

        SortedMap<Object,Object> requestMap = new TreeMap<Object,Object>();
        // 公众好appId
        requestMap.put("appid",Constant.APP_ID);
        // 商户id
        requestMap.put("mch_id",Constant.MERCHANT_ID);
        // 随机字符串
        requestMap.put("nonce_str",WechatUtil.getNonceStr());
        // 商品名称
//        requestMap.put("body",orders.getPName());
        requestMap.put("body","ceshi");
        // 订单编号
//        requestMap.put("out_trade_no",orderId+"");
        requestMap.put("out_trade_no",UUID.next()+"");
        // 商品总价

//        requestMap.put("total_fee",WechatUtil.getTotalFee(orders.getAmount())+"");
        requestMap.put("total_fee",WechatUtil.getTotalFee(1.00)+"");
        // 请求ip
        requestMap.put("spbill_create_ip",WechatUtil.getIpAddr(request));
        // 异步回调地址
        requestMap.put("notify_url","/test");
        // 支付方式
        requestMap.put("trade_type","JSAPI");
        // 用户openId
        requestMap.put("openid","oFPHG1GftBSAiHC-g9Xl0VP5K_Ro");
        // 创建签名
        String sign = WechatUtil.createSign(requestMap,Constant.MCHKEY);
        requestMap.put("sign",sign);
        // 生成请求参数
        String xmlStr = WechatUtil.getRequestXML(requestMap);
        System.out.println(xmlStr);
        // 统一下单
        String post = HttpUtils.post("https://api.mch.weixin.qq.com/pay/unifiedorder", xmlStr);

        // 装换获取请求结果
        Map resp = WechatUtil.doXMLParse(post);
        // 获取返回码，返回状态
        String code = resp.get("return_code").toString();

        String msg = resp.get("return_msg").toString();

        if ("SUCCESS".equals(code)) {
            // 成功
            String nonce_str = resp.get("nonce_str").toString();
            String result_code = resp.get("result_code").toString();
            if ("SUCCESS".equals(result_code)) {
                String prepay_id = resp.get("prepay_id").toString();
                // 封装了开始调起支付接口数据
                SortedMap<Object, Object> packageParam = new TreeMap<Object, Object>();
                packageParam.put("appId", Constant.APP_ID); //微信开发平台应用ID 下面参数都不完整  需要写入自己的参数


//                packageParam.put("timeStamp", Constant.MERCHANT_ID);
/*                packageParam.put("prepayid", prepay_id);// 商品描述*/


                packageParam.put("package", "prepay_id="+prepay_id);// 商户订单号
                packageParam.put("nonceStr", nonce_str);
                packageParam.put("signType", "MD5 ");// 商户订单号
                packageParam.put("timeStamp", WechatUtil.getTimeStamp());
                String sign1 = WechatUtil.createSign("UTF-8", packageParam, Constant.MCHKEY);// 这里是二次签名    //应用对应的密钥
                // 处理交易订单信息 // 前台要拿到去调起微信支付，如果这个错了的话会在前台报签名错误
                Map<String, Object> jsonMap = new TreeMap<String, Object>();
                jsonMap.put("appId", Constant.APP_ID);//微信开发平台应用ID 下面参数都不完整  需要写入自己的参数
                jsonMap.put("nonceStr", nonce_str);
                System.out.println("参数为："+prepay_id);
                jsonMap.put("package", "prepay_id="+prepay_id);
                jsonMap.put("timeStamp", WechatUtil.getTimeStamp());
                jsonMap.put("signType","MD5");
                jsonMap.put("paySign", sign1);
                resultMap = jsonMap;
                System.out.println("支付请求封装完毕");
            }
        }
        return new ModelAndView("test/index",resultMap);
    }
}
