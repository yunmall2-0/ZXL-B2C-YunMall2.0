package com.management.admin.controller;

import com.management.admin.biz.ICategorysService;
import com.management.admin.biz.IProductService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Categorys;
import com.management.admin.entity.db.Product;
import com.management.admin.entity.db.ProductParameterRelation;
import com.management.admin.entity.dbExt.ProductDetail;
import com.management.admin.entity.template.SessionModel;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.DateUtil;
import com.management.admin.utils.JsonUtil;
import com.management.admin.utils.StringUtil;
import com.management.admin.utils.web.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.List;

@Controller
@RequestMapping("product")
public class ProductController {

    @Autowired
    private IProductService productService;
    @Autowired
    private ICategorysService categorysService;

    @GetMapping("index")
    public String index() {
        return "product/index";
    }

    /**
     * 查询分页 狗蛋 2019年2月20日10:59:27
     *
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping("getLimit")
    @ResponseBody
    public JsonArrayResult<ProductDetail> getLimit(HttpServletRequest request, HttpServletResponse response,
                                                   Integer page, String limit, String condition, String beginTime,
                                                   String endTime,String category) {
        SessionModel session = SessionUtil.getSession(request);
        Integer userId = session.getUserId();
        Integer count = 0;
        List<ProductDetail> list = productService.getLimit(userId, page, limit, condition, beginTime, endTime,category);
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, list);
        if (StringUtil.isBlank(condition)
                && StringUtil.isBlank(beginTime)
                && StringUtil.isBlank(endTime)
                && StringUtils.isBlank(category)) {
            count = productService.getCount(userId);
        } else{
            count = productService.getLimitCount(userId, condition, beginTime, endTime,category);
        }
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 商品发布界面
     *
     * @return
     */
    @GetMapping("publish")
    public String publish() {
        return "product/publish";
    }

    /**
     * 添加商品 狗蛋 2019年2月23日09:39:37
     *
     * @param productStr
     * @return
     */
    @PostMapping("addProduct")
    @ResponseBody
    public JsonResult addProduct(String productStr,String productParameterStr,HttpServletRequest request,HttpServletResponse response) {
        SessionModel session = SessionUtil.getSession(request);
        Product model = JsonUtil.getModel(productStr, Product.class);
        List<ProductParameterRelation> valueList = JsonUtil.getModelAsList(productParameterStr,ProductParameterRelation.class);
        model.setShopId(session.getUserId());
        int insert = productService.insertProduct(model,valueList);
        if (insert > 0) {
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

    /**
     * 删除商品 狗蛋 2019年2月20日17:08:26
     *
     * @param productIdStr
     * @return
     */
    @GetMapping("delete")
    @ResponseBody
    public JsonResult delete(String productIdStr) {
        if (StringUtils.isNotEmpty(productIdStr)) {
            Product p = new Product();
            p.setProductId(Long.parseLong(productIdStr));
            int delete = productService.delete(p);
            if (delete > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 商品上架 狗蛋 2019年2月23日09:39:31
     *
     * @param productIdStr
     * @return
     */
    @GetMapping("putaway")
    @ResponseBody
    public JsonResult putaway(String productIdStr) {
        if (StringUtils.isNotEmpty(productIdStr)) {
            Integer integer = productService.updateStatus(Long.parseLong(productIdStr), 1);
            if (integer > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 商品下架 狗蛋 2019年2月23日09:39:26
     *
     * @param productIdStr
     * @return
     */
    @GetMapping("soldOut")
    @ResponseBody
    public JsonResult soldOut(String productIdStr) {
        if (StringUtils.isNotEmpty(productIdStr)) {
            Integer integer = productService.updateStatus(Long.parseLong(productIdStr), 2);
            if (integer > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 根据编号获取商品信息，用来编辑 狗蛋 2019年2月23日09:39:22
     *
     * @param productId
     * @return
     */
    @GetMapping("getById")
    @ResponseBody
    public JsonResult getById(String productId) {
        if (StringUtils.isNotEmpty(productId)) {
            ProductDetail product1 = productService.selectProductDetailById(Long.parseLong(productId));
            if (product1 == null) {
                return JsonResult.failing();
            }
            ProductDetail productDetail = new ProductDetail();
            BeanUtils.copyProperties(product1, productDetail);
            productDetail.setProductIdStr(product1.getProductId().toString());
            return new JsonResult(200, productDetail);
        }
        return JsonResult.failing();
    }

    /**
     * 根据编号获取商品信息，用来展示详情 狗蛋 2019年2月21日09:41:21
     *
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("get")
    public String get(String productId, final Model model) {
        if (StringUtils.isNotEmpty(productId)) {
            ProductDetail product1 = productService.selectProductDetailById(Long.parseLong(productId));
            if (product1 == null) {
                model.addAttribute("errorText", "查询结果为空");
                return "errorPage/index";
            }
            ProductDetail productDetail = new ProductDetail();
            // 克隆
            BeanUtils.copyProperties(product1, productDetail);
            // 包装long date属性
            productDetail.setProductIdStr(product1.getProductId().toString());
            productDetail.setAddTimeStr(DateUtil.getFormatDateTime(product1.getAddTime(), "yyyy-MM-dd HH:mm:ss"));
            productDetail.setEditTimeStr(DateUtil.getFormatDateTime(product1.getEditTime(), "yyyy-MM-dd HH:mm:ss"));
            // 查询包装详情分类名称
            Categorys categorys = categorysService.selectCategoryById(productDetail.getCategory());
            Categorys parentCategorys = categorysService.selectCategoryById(categorys.getParentId());
            productDetail.setCategoryName(parentCategorys.getCategoryName()+" -> "+categorys.getCategoryName());
            model.addAttribute("product", productDetail);
            return "product/detail";
        }
        model.addAttribute("errorText", "商品编号为空");
        return "errorPage/index";
    }

    /**
     * 修改商品信息 狗蛋 2019年2月23日09:38:40
     * @param productStr
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public JsonResult update(String productStr,String productParameterStr) {
        System.out.println(productParameterStr);
        if (StringUtils.isNotEmpty(productStr)) {
            Product model = JsonUtil.getModel(productStr, Product.class);
            List<ProductParameterRelation> parameterRelationList = JsonUtil.getModelAsList(productParameterStr,ProductParameterRelation.class);
            int update = productService.updateProduct(model,parameterRelationList);
            if (update > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 商品批量上架 狗蛋 2019年2月23日09:38:57
     * @param productIds
     * @return
     */
    @GetMapping("putaways")
    @ResponseBody
    public JsonResult putaways(String productIds) {
        if (StringUtils.isNotEmpty(productIds)) {
            String[] split = productIds.split(",");
            Integer result = 0;
            for (String productId : split) {
                result = productService.updateStatus(Long.parseLong(productId), 1);
            }
            if (result > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 商品批量下架 狗蛋 2019年2月23日09:39:07
     * @param productIds
     * @return
     */
    @GetMapping("soldOuts")
    @ResponseBody
    public JsonResult soldOuts(String productIds) {
        if (StringUtils.isNotEmpty(productIds)) {
            String[] split = productIds.split(",");
            Integer result = 0;
            for (String productId : split) {
                result = productService.updateStatus(Long.parseLong(productId), 2);
            }
            if (result > 0) return JsonResult.successful();
            return JsonResult.failing();
        }
        return JsonResult.failing();
    }

    /**
     * 获取所有可用商品 狗蛋 2019年3月1日17:31:41
     * @return
     */
    @GetMapping("getShowProductList")
    @ResponseBody
    public JsonResult getShowProductList(){
        List<ProductDetail> productList = productService.getShowProductList();
        if(productList.size()<=0) throw new MsgException("可用商品列表为空！");
        return new JsonResult(200,productList);
    }
}
