package com.management.admin.controller;

import com.alibaba.fastjson.JSON;
import com.management.admin.biz.IProductParameterService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.ProductParameter;
import com.management.admin.entity.dbExt.ProductDetail;
import com.management.admin.entity.dbExt.ProductParameterDetail;
import com.management.admin.entity.template.SessionModel;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.StringUtil;
import com.management.admin.utils.web.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.List;

@Controller
@RequestMapping("productParameter")
public class ProductParameterController {


    @Autowired
    private IProductParameterService parameterService;

    /**
     * 列表页
     *
     * @return
     */
    @GetMapping("index")
    public String index() {
        return "productParameter/index";
    }

    /**
     * 查询分页 狗蛋 2019年2月20日10:59:27
     *
     * @param page
     * @param limit
     * @param condition
     * @return
     */
    @RequestMapping("getLimit")
    @ResponseBody
    public JsonArrayResult<ProductParameterDetail> getLimit(HttpServletRequest request, HttpServletResponse response,
                                                            Integer page, String limit, String condition, Integer categoryId) {
        Integer count = 0;
        List<ProductParameterDetail> list = parameterService.getLimit(page, limit, condition, categoryId);

        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, list);
        if (StringUtil.isBlank(condition) && categoryId != null) {
            count = parameterService.getCount();
        } else {
            count = parameterService.getLimitCount(condition, categoryId);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 添加页面
     *
     * @return
     */
    @GetMapping("add")
    public String add() {
        return "productParameter/add";
    }

    /**
     * 添加
     * @param name  名称
     * @param categoryId 分类编号
     * @return
     */
    @PostMapping("addParameter")
    @ResponseBody
    public JsonResult addParameter(String name,Integer categoryId){
        if(StringUtils.isBlank(name) || categoryId == null) throw new MsgException("参数不合法！");
        parameterService.add(name,categoryId);
        return JsonResult.successful();
    }

    /**
     * 修改界面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("edit")
    public String edit(Integer id, final Model model){
        if(id == null) throw new MsgException("参数为空");
        ProductParameter parameter = parameterService.selectById(id);
        if(parameter == null) throw new MsgException("查询失败！");
        model.addAttribute("parameter",parameter);
        return "productParameter/edit";
    }

    /**
     * 修改商品参数
     * @param parameter
     * @return
     */
    @PostMapping("updateParameter")
    @ResponseBody
    public JsonResult updateParameter(ProductParameter parameter){
        Integer result = parameterService.updateParameter(parameter);
        if(result<=0) throw new MsgException("更新失败！");
        return JsonResult.successful();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("deleteParameter")
    @ResponseBody
    public JsonResult deleteParameter(Integer id){
        Integer result = parameterService.deleteParameter(id);
        if(result<=0) throw new MsgException("删除失败");
        return JsonResult.successful();
    }

    @GetMapping("getByCategoryId")
    @ResponseBody
    public JsonResult getByCategoryId(Integer categoryId){
        if(categoryId == null) throw new MsgException("参数不合法");
        List<ProductParameter> productParameters = parameterService.selectByCategoryId(categoryId);
        return new JsonResult(200,productParameters);
    }
}
