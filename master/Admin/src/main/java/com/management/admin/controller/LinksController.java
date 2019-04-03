package com.management.admin.controller;

import com.management.admin.biz.ILinksService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Links;
import com.management.admin.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "links")
public class LinksController {
    @Autowired
    private ILinksService linksService;

    /**
     * 跳转商品的链接页面 钉子 2019年2月21日11:30:50
     * @return
     */
    @RequestMapping(value = "selectAllLinkPage")
    public String selectAllLinksByProductIdPage(){
        return "links/index";
    }

    /**
     * 跳转添加链接页面 钉子 2019年2月21日11:32:24
     * @return
     */
    @RequestMapping(value = "addLinkPage")
    public String addLinksPage(){
        return "links/addLinks";
    }

    /**
     * 分页查看商品的链接 钉子 2019年2月21日11:34:29
     * @param productId
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "selectAllLinks")
    @ResponseBody
    public JsonArrayResult<Links> selectAllKhamwisByProduct(String productId, Integer page, String limit, String condition, String beginTime, String endTime){
        Integer count=0;
        List<Links> list=linksService.selectAllLinksByProductId(Long.valueOf(productId),page,limit,condition,beginTime,endTime);
        JsonArrayResult jsonArrayResult=new JsonArrayResult(0,list);
        if (StringUtil.isBlank(condition)&&StringUtil.isBlank(beginTime)&&StringUtil.isBlank(endTime)){
            count=linksService.selectLinksCount(Long.valueOf(productId));
        }else {
            count=linksService.selectLimitCount(Long.valueOf(productId),condition,beginTime,endTime);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }
    /**
     * 单个添加链接  钉子 2019年2月21日11:35:04
     * @param
     * @return
     */
    @RequestMapping(value = "addLinks")
    @ResponseBody
    public JsonResult addLinks(String productId, String link){
        Links links=new Links();
        links.setProductId(Long.valueOf(productId));
        links.setLink(link);
        links.setAddTime(new Date());
        int i=linksService.insertLinks(links);
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
    @RequestMapping(value = "addManyLinks")
    @ResponseBody
    public JsonResult addManyLinks(MultipartFile file, String productId){
        if (!file.isEmpty()){
            int i=linksService.insertManyLinks(productId,file);
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
     * 删除链接 钉子 2019年2月21日15:09:15
     * @param linkId
     * @return
     */
    @RequestMapping(value = "deleteLinks")
    @ResponseBody
    public JsonResult deleteLinks(Integer linkId){
        int i=linksService.deleteLinks(linkId);
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }

    /**
     * 删除多个链接 钉子 2019年2月21日15:09:03
     * @param linkId
     * @return
     */
    @RequestMapping(value = "deleteManyLinks")
    @ResponseBody
    public JsonResult deleteManyLinks(String linkId){
        int i=0;
        String[] s=linkId.split(",");
        if (s.length<=1&&s.length>0){
            i=linksService.deleteLinks(Integer.parseInt(s[0]));
        }else{
            i=linksService.deleteManyLinks(s);
        }
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }
}
