package com.management.admin.controller;

import com.management.admin.biz.IKhamwisService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Khamwis;
import com.management.admin.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "khamwis")
public class KhamwisController {
    @Autowired
    private IKhamwisService khamwisService;

    /**
     * 跳转商品的卡密页 钉子 2019年2月20日11:59:14
     * @return
     */
    @RequestMapping(value = "selectAllKhamwisPage")
    public String selectAllKhamwisByProductPage(){
        return "khamwis/index";
    }

    /**
     * 跳转添加卡密库页面 钉子 2019年2月20日13:17:11
     * @return
     */
    @RequestMapping(value = "addKhawisPage")
    public String addKhamwisPage(){
        return "khamwis/addKhamwis";
    }
    /**
     * 分页查询商品的卡密 钉子 2019年2月20日11:06:49
     * @param productId
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "selectAllKhamwis")
    @ResponseBody
    public JsonArrayResult<Khamwis> selectAllKhamwisByProduct(String productId,Integer page, String limit, String condition, String beginTime, String endTime){
        Integer count=0;
        List<Khamwis> list=khamwisService.selectKhamwisByProductId(Long.valueOf(productId),page,limit,condition,beginTime,endTime);
        JsonArrayResult jsonArrayResult=new JsonArrayResult(0,list);
        if (StringUtil.isBlank(condition)&&StringUtil.isBlank(beginTime)&&StringUtil.isBlank(endTime)){
            count=khamwisService.selectKhamwisCount(Long.valueOf(productId));
        }else {
            count=khamwisService.selectLimitCount(Long.valueOf(productId),condition,beginTime,endTime);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 单个添加卡密  钉子 2019年2月20日15:14:39
     * @param
     * @return
     */
    @RequestMapping(value = "addKhamwis")
    @ResponseBody
    public JsonResult addKhamwis(String productId,String cardNumber,String cardPwd){
        Khamwis khamwis=new Khamwis();
        khamwis.setProductId(Long.valueOf(productId));
        khamwis.setCardNumber(cardNumber);
        khamwis.setCardPwd(cardPwd);
        khamwis.setAddTime(new Date());
        int i=khamwisService.insertKhamwis(khamwis);
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }
    /**
     * 批量添加卡密  钉子 2019年2月20日15:14:39
     * @param file
     * @return
     */
    @RequestMapping(value = "addManyKhamwis")
    @ResponseBody
    public JsonResult addManyKhamwis(MultipartFile file,String productId){
        if (!file.isEmpty()){
            int i=khamwisService.insertManyKhamwis(productId,file);
            if (i>0){
                return JsonResult.successful();
            }else{
                return JsonResult.failing();
            }
        }else {
            return new JsonResult(300, "文件为空");
        }
    }
    /**
     * 删除卡密
     * @param cardId
     * @return
     */
    @RequestMapping(value = "deleteKhamwis")
    @ResponseBody
    public JsonResult deleteKhamwis(Integer cardId){
        int i=khamwisService.deleteKhamwis(cardId);
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }

    /**
     * 批量删除卡密 钉子 2019年2月21日14:53:04
     * @param cardId
     * @return
     */
    @RequestMapping(value = "deleteManyKhamwis")
    @ResponseBody
    public JsonResult deleteManyKhamwis(String cardId){
        int i=0;
        String[] s=cardId.split(",");
        if (s.length<=1&&s.length>0){
            i=khamwisService.deleteKhamwis(Integer.parseInt(s[0]));
        }else{
            i=khamwisService.deleteManyKhamwis(s);
        }
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }
}
