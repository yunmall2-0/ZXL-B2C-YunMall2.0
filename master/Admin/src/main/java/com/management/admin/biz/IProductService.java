package com.management.admin.biz;

import com.management.admin.entity.db.Product;
import com.management.admin.entity.db.ProductParameterRelation;
import com.management.admin.entity.dbExt.ProductDetail;
import com.management.admin.entity.dbExt.ProductDetails;
import io.swagger.models.auth.In;

import java.util.List;

public interface IProductService extends IBaseService<Product> {

    /**
     * 查询分页
     * @param shopId
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    List<ProductDetail> getLimit(Integer shopId, Integer page, String limit, String condition,
                                 String beginTime, String endTime,String category);

    /**
     * 查询分页记录数
     * @param shopId
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer getLimitCount(Integer shopId,String condition, String beginTime, String endTime,String category);

    /**
     * 查询分页总记录数
     * @param shopId
     * @return
     */
    Integer getCount(Integer shopId);

    Integer updateStatus(Long productId, Integer status);

    /**
     * 根据Id查询商品详情 Timor 2019-2-22 10:46:59
     * @param productId
     * @return
     */
    Product selectProductById(Long productId);

    /**
     * 查看所有已上架的未删除的商品信息 狗蛋 2019年2月28日15:45:41
     * @return
     */
    List<ProductDetail> getProductByCategoryId(Integer categoryId);

    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日17:28:44
     * @return
     */
    List<ProductDetail> selectHotProducts();
    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日17:28:44
     * @return
     */
    List<ProductDetail> getShowProductList();
    /**
     * 根据商品Id查询商品 钉子 2019年3月1日16:55:02
     *
     * @param productId
     * @return
     */
    ProductDetail selectProductByProductId(Long productId);

    /**
     * 首页中根据商品类型查找商品 钉子 2019年3月11日11:42:32
     * @return
     */
    List<ProductDetails> selectProductsByType();
    /**
     * 根据商品类型查找所有商品 钉子 2019年3月13日16:38:42
     * @param type
     * @return
     */
    List<Product> selectProductsByOneType(Integer type);

    /**
     * 获取商品信息根据编号（包含参数列表）
     * @param productId
     * @return
     */
    ProductDetail selectProductDetailById(Long productId);

    /**
     * 商品发布
     * @param product
     * @param valueList
     * @return
     */
    Integer insertProduct(Product product, List<ProductParameterRelation> valueList);

    /**
     * 修改商品信息
     */
    Integer updateProduct(Product product,List<ProductParameterRelation> parameterRelationList);
}
