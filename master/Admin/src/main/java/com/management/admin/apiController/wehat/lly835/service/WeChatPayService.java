package com.management.admin.apiController.wehat.lly835.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.lly835.bestpay.utils.JsonUtil;
import com.management.admin.entity.dbExt.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName WeChatPayService
 * @Description
 * @Author ZXL01
 * @Date 2018/12/22 12:59
 * Version 1.0
 **/

@Slf4j
@Service
public class WeChatPayService {

    @Autowired
    private BestPayServiceImpl bestPayService;
    //微信支付发起
    public PayResponse create(OrderDTO orderDTO) {
        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);//支付方式，微信公众账号支付
        payRequest.setOrderId(orderDTO.getOrderId());//订单号.
        payRequest.setOrderName(orderDTO.getPayName());//订单名字.
        payRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());//订单金额.
        payRequest.setOpenid(orderDTO.getBuyerOpenid());//微信openid, 仅微信支付时需要

        log.info("【微信支付】request={}", JsonUtil.toJson(payRequest));//将payRequest格式化一下，再显示在日志上，便于观看数据
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("【微信支付】response={}", JsonUtil.toJson(payResponse));//将payResponse格式化一下，再显示在日志上，便于观看响应后返回的数据);
        return payResponse;

    }

    public RefundResponse refund(OrderDTO orderDTO) {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(orderDTO.getOrderId());
        refundRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("【微信退款】request={}", JsonUtil.toJson(refundRequest));

        RefundResponse refundResponse = bestPayService.refund(refundRequest);
        log.info("【微信退款】response={}", JsonUtil.toJson(refundResponse));

        return refundResponse;
    }
}
