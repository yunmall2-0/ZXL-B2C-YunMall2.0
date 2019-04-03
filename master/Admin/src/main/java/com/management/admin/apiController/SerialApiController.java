package com.management.admin.apiController;

import com.management.admin.biz.impl.PayServiceImpl;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Pays;
import com.management.admin.entity.dbExt.OrdersDatails;
import com.management.admin.utils.web.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName SerialApiController
 * @Description
 * @Author ZXL01
 * @Date 2019/2/27 11:12
 * Version 1.0
 **/
@RestController
@RequestMapping("api/serial")
public class SerialApiController {

    @Autowired
    private PayServiceImpl payService;


    /**
     * 分页显示用户交易流水明细(模糊查询) Timor  2019-2-27 16:43:36
     * @param request
     * @param limit
     * @param page
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "showUserPays")
    public JsonResult queryOrdersByUid(HttpServletRequest request, String limit, Integer page, String condition,String beginTime,String endTime){
        Integer userId= SessionUtil.getSession(request).getUserId();
        Integer count=0;
        List<Pays> ordersLimit = payService.selectPaysByPages(page, limit, condition,userId,beginTime,endTime);
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, ordersLimit);
        count = payService.getPageCount(condition,userId,beginTime,endTime);
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 根据消费类型得到用户相应的消费总额  Timor  2019-2-27 16:46:27
     * @param request
     * @param tradeType
     * @return
     */
    @GetMapping(value = "getTotal")
    public JsonResult queryTotalByType(HttpServletRequest request,Integer tradeType){
        Double total=payService.queryConsumeByType(request,tradeType);
        return new JsonArrayResult().successful(total);
    }


}
