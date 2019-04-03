package com.management.admin.apiController;

import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.template.SessionModel;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.IdWorker;
import com.management.admin.utils.web.SessionUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/recharge")
public class RechargeApiController {

    /**
     * 用户充值 狗蛋 2019年3月2日13:17:29
     * @param amount
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("recharge")
    public JsonResult recharge(Double amount, HttpServletRequest request, HttpServletResponse response) throws Exception{
        SessionModel session = SessionUtil.getSession(request);
        if(amount == null) throw new MsgException("充值金额不能为空！");
        Orders orders = new Orders();
        orders.setPName("充值"+amount+"元");
        orders.setAmount(amount);
        Long orderId = IdWorker.getFlowIdWorkerInstance().nextId();
        orders.setOrderId(orderId.toString());
        orders.setUid(session.getUserId());
        orders.setRemark("支付宝充值"+amount+"元");
        orders.setPid(new Long("619288586956574720"));
        // TODO 发起支付
        return null;
    }

    public static void main(String[] args) throws Exception{
        Long orderId = IdWorker.getFlowIdWorkerInstance().nextId();
        System.out.println(orderId);
    }

}
