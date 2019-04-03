package com.management.admin.controller;

import com.google.gson.JsonArray;
import com.management.admin.biz.impl.ExamineServiceImpl;
import com.management.admin.biz.impl.WithdrawServiceImpl;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Examine;
import com.management.admin.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName examineController
 * @Description
 * @Author ZXL01
 * @Date 2019/1/10 20:44
 * Version 1.0
 **/

@Controller
@RequestMapping("examine")
public class ExamineController {

    @Autowired
    private ExamineServiceImpl examineService;

    @Autowired
    private WithdrawServiceImpl withdrawService;

    /**
     * 跳转到审批管理界面
     */
     @GetMapping(value = "index")
     private String getBgLogin(){

        return "withdraw/examine";
     }

    /**
     * 查询所有的用户基本信息 提莫 2018-11-29 11:04:45
     * @return List<Users>
     */

    @GetMapping(value = "getexamines")
    @ResponseBody
    public  JsonArrayResult<Examine> getAllUsers(Integer page, String limit, String condition, String beginTime, String endTime){

        Integer count = 0;
        List<Examine> list=examineService.selectAllExamine(page, limit, condition,beginTime, endTime);
        for(Examine examine:list){
            examine.setStrPayId(examine.getSystemRecordId().toString());
        }
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, list);
        count = examineService.getLimitCount(condition,beginTime, endTime);
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 修改状态为已经通过审核
     * @param shenpiId
     * @return
     */
    @GetMapping("updatestatus")
    @ResponseBody
    public JsonResult updateStatus(Integer shenpiId,String channelRecordId){
        Integer result= withdrawService.passCheck(shenpiId,channelRecordId);
        if(result>0){
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }


    /**
     * 修改状态为审核失败
     * @param shenpiId
     * @param remark
     * @return
     */
    @GetMapping("updatestatusFail")
    @ResponseBody
    public JsonResult updatestatusFail(Integer shenpiId,String remark){
        Integer result=withdrawService.auditFailure(shenpiId,remark);
        if(result>0){
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

}
