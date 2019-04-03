package com.management.admin.apiController;

import com.management.admin.biz.IProductService;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Product;
import com.management.admin.entity.dbExt.ProductDetail;
import com.management.admin.entity.dbExt.ProductDetails;
import com.management.admin.exception.MsgException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private IProductService productService;

    /**
     * 根据商品编号查询商品信息 狗蛋 2019年2月28日16:19:13
     * @param productId
     * @return
     */
    @GetMapping("getProductById")
    public JsonResult getProductById(String productId){
        if(StringUtils.isBlank(productId)) throw new MsgException("商品编号为空不能获取商品信息！");
        ProductDetail product = productService.selectProductByProductId(Long.parseLong(productId));
        product.setProductIdStr(product.getProductId().toString());
        if(product==null) throw new MsgException("获取商品信息失败！");
        return new JsonResult(200,product);
    }

    /**
     * 根据分类编号获取相应的商品 狗蛋 2019年2月28日16:07:51
     * @param categoryId
     * @return
     */
    @RequestMapping("getProductByCategoryId")
    public JsonResult getProductByCategoryId(Integer categoryId){
        if(categoryId==null) throw new MsgException("分类编号为空！");
        List<ProductDetail> products = productService.getProductByCategoryId(categoryId);
        if(products.size()<=0) throw new MsgException("没有商品信息");
        return new JsonResult(200,products);
    }

    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日09:34:56
     * @return
     */
    @GetMapping("getHotProducts")
    public JsonResult getHotProducts(){
        List<ProductDetail> hotProducts = productService.selectHotProducts();
        if(hotProducts.size()<=0) throw new MsgException("没有热推商品信息！");
        return new JsonResult(200,hotProducts);
    }

    /**
     * 首页中根据类型获取商品 钉子 2019年3月11日12:03:03
     * @return
     */
    @GetMapping(value = "getProductsByType")
    public JsonResult getProductsByType(){
        List<ProductDetails> productDetails=productService.selectProductsByType();
        return new JsonResult(200,productDetails);
    }
    /**
     * 根据类型获取所有商品 钉子 2019年3月13日16:40:24
     * @return
     */
    @GetMapping(value = "getProductsByOneType")
    public JsonResult getProductsByOneType(Integer type){
        List<Product> product=productService.selectProductsByOneType(type);
        return new JsonResult(200,product);
    }


    /**
     * 查询商品详情，包含参数信息
     * @param productId
     * @return
     */
    @GetMapping("getProductDetail")
    public JsonResult getProductDetail(String productId){
        ProductDetail productDetail = productService.selectProductDetailById(Long.parseLong(productId));
        if(productDetail == null) throw new MsgException("查询失败");
        return new JsonResult(200,productDetail);
    }
}
