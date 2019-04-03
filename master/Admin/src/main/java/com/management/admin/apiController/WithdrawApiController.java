package com.management.admin.apiController;

import com.management.admin.biz.impl.WithdrawServiceImpl;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Withdraw;
import com.management.admin.utils.JsonUtil;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;

/**
 * @ClassName WithdrawApiController
 * @Description TODO
 * @Author ZXL01
 * @Date 2019/2/28 17:42
 * Version 1.0
 **/

@Controller
@ResponseBody
@RequestMapping("api/withdraw")
public class WithdrawApiController {

    @Autowired
    private WithdrawServiceImpl withdrawService;


    /**
     * 查询用户绑定的账户信息  Timor  2019-2-28 17:52:56
     * @param request
     * @return
     */
    @GetMapping(value = "queryAccount")
    public JsonResult queryAccount(HttpServletRequest request){
        String account=withdrawService.selectAccount(request);
        return new JsonArrayResult().successful(account);
    }


    /**
     * 绑定用户账户信息返回账户信息  Timor  2019-2-28 17:45:09
     * @param request
     * @param withdraw
     * @return
     */
    @PostMapping(value = "bindAccount")
    public JsonResult bindAccount(HttpServletRequest request,String  withdraw){
        Withdraw withdraw1=JsonUtil.getModel(withdraw,Withdraw.class);
        Integer  result=withdrawService.bindAccount(request,withdraw1);
        if(result>0){
            return JsonResult.successful();
        }
        return  JsonResult.failing();
    }

    /**
     * 用户提现   Timor   2019-3-1 16:39:21
     * @param request
     * @param amount
     * @return
     */
    @GetMapping (value = "deposit")
    public JsonResult withdraw(HttpServletRequest request,Double amount){
        Integer result=withdrawService.withdraw(request,amount);
        if(result>0){
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

}
