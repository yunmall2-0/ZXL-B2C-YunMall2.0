package com.management.admin.repository;

import com.management.admin.entity.db.Product;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends MyMapper<Product> {
    /**
     * 查询分页
     *
     * @param page
     * @param limit
     * @param trade_date_begin
     * @param trade_date_end
     * @param
     * @return
     */
    @Select("select * from products where ${condition} and is_delete = 0" +
            " and shop_Id=#{shopId} ORDER BY add_Time DESC LIMIT #{page},${limit}")
    List<Product> selectLimit(@Param("shopId") Integer shopId,
                              @Param("page") Integer page,
                              @Param("limit") String limit,
                              @Param("beginTime") String trade_date_begin,
                              @Param("endTime") String trade_date_end,
                              @Param("condition") String condition,
                              @Param("category") String category);

    /**
     * 查询分页记录总数 韦德 2018年8月27日09:52:40
     *
     * @param
     * @param trade_date_begin
     * @param trade_date_end
     * @param
     * @return
     */
    @Select("SELECT COUNT(product_Id) FROM products where " +
            "${condition} and is_delete=0 and shop_Id=#{shopId}")
    int getLimitCount(@Param("shopId") Integer shopId
            , @Param("beginTime") String trade_date_begin
            , @Param("endTime") String trade_date_end
            , @Param("condition") String condition
            , @Param("category") String category);


    /**
     * 记录数
     *
     * @param
     * @return
     */
    @Select("select count(*) FROM products where is_delete=0 and shop_Id=#{shopId}")
    Integer getCount(Integer shopId);

    /**
     * 修改商品信息
     *
     * @param product
     * @return
     */
    @Update("update products set main_Image=#{mainImage}," +
            "product_Name=#{productName},amount=#{amount}," +
            "description=#{description},category=#{category}," +
            "edit_Time=#{editTime},remark=#{remark},sort=#{sort}," +
            "is_Hot=#{isHot},type=#{type} where product_Id=#{productId}")
    Integer update(Product product);

    /**
     * 修改商品状态
     *
     * @param productId
     * @param status
     * @return
     */
    @Update("update products set status=#{status} where product_Id=#{productId}")
    Integer updateStatus(@Param("productId") Long productId, @Param("status") Integer status);

    /**
     * 删除商品
     *
     * @param productId
     * @return
     */
    @Update("update products set is_delete=1 where product_Id=#{productId}")
    Integer deleteProduct(Long productId);

    /**
     * 跟据商品编号查询商品
     * @param productId
     * @return
     */
    @Select("select * from products where product_id=#{productId} and is_delete=0")
    Product selectById(Long productId);

    /**
     * 根据分类已上架的未删除的商品信息 狗蛋 2019年2月28日15:45:41
     * @return
     */
    @Select("select * from products where is_delete=0" +
            " and status = 1 and category = #{categoryId}")
    List<Product> getProductByCategoryId(Integer categoryId);

    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日17:27:33
     * @return
     */
    @Select("select * from products where is_delete=0" +
            " and status=1 and is_hot=1")
    List<Product> selectHotProducts();

    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日17:27:33
     * @return
     */
    @Select("select * from products where is_delete=0" +
            " and status=1")
    List<Product> getShowProductList();

    /**
     * 根据商品类型查找商品 钉子 2019年3月11日11:39:52
     * @param type
     * @return
     */
    @Select("select * from products where is_delete=0 and status=1 and type=#{type} ORDER BY sort ASC LIMIT 3")
    List<Product> selectProductsByType(Integer type);

    /**
     * 根据商品类型查找所有商品 钉子 2019年3月13日16:38:42
     * @param type
     * @return
     */
    @Select("select * from products where is_delete=0 and status=1 and type=#{type} ORDER BY sort ASC")
    List<Product> selectProductsByOneType(Integer type);




}
