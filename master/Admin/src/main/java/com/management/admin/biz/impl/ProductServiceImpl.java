package com.management.admin.biz.impl;

import com.management.admin.biz.IProductService;
import com.management.admin.entity.db.*;
import com.management.admin.entity.dbExt.ProductDetail;
import com.management.admin.entity.dbExt.ProductDetails;
import com.management.admin.entity.dbExt.ProductParameterDetail;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.*;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product> implements IProductService {

    private ProductMapper productMapper;
    private KhamwisMapper khamwisMapper;
    private LinksMapper linksMapper;
    private CategorysMapper categorysMapper;
    private ProductParameterMapper parameterMapper;
    private ProductParameterRelationMapper parameterRelationMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, KhamwisMapper khamwisMapper, LinksMapper linksMapper, CategorysMapper categorysMapper, ProductParameterMapper parameterMapper, ProductParameterRelationMapper parameterRelationMapper) {
        this.productMapper = productMapper;
        this.khamwisMapper = khamwisMapper;
        this.linksMapper = linksMapper;
        this.categorysMapper = categorysMapper;
        this.parameterMapper = parameterMapper;
        this.parameterRelationMapper = parameterRelationMapper;
    }

    /**
     * 获取数据  2018年8月13日13:26:57
     *
     * @param param
     * @return
     */
    @Override
    public Product get(Product param) {
        if (param != null) {
            return productMapper.selectById(param.getProductId());
        }
        return super.get(param);
    }

    /**
     * 获取全部数据  2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<Product> getList() {
        return super.getList();
    }

    /**
     * 分页获取全部数据  2018年8月13日13:27:17
     *
     * @param page
     * @param limit
     * @param condition
     * @return
     */
    @Override
    public List<ProductDetail> getLimit(Integer shopId, Integer page, String limit, String condition,
                                        String beginTime, String endTime, String category) {
        // 计算分页位置
        page = ConditionUtil.extractPageIndex(page, limit.toString());
        String where = extractLimitWhere(condition, beginTime, endTime, category);
        List<Product> list = productMapper.selectLimit(shopId, page, limit, beginTime, endTime, where, category);
        List<ProductDetail> detailList = new ArrayList<>();
        list.forEach(item -> {
            ProductDetail productDetail = new ProductDetail();
            BeanUtils.copyProperties(item, productDetail);
            productDetail.setProductIdStr(item.getProductId().toString());
            if (item.getType().equals(1)) {
                List<Khamwis> khamwis = khamwisMapper.selectKhamwiByProductId(item.getProductId());
                productDetail.setInventory(khamwis.size());
                if (khamwis.size() > 0) {
                    productDetail.setKhamwisList(khamwis);
                }
            }
            if (item.getType().equals(2)) {
                List<Links> links = linksMapper.selectLinksByProductId(item.getProductId());
                productDetail.setInventory(links.size());
                if (links.size() > 0) {
                    productDetail.setLinksList(links);
                }
            }
            detailList.add(productDetail);
        });
        return detailList;
    }

    /**
     * 提取分页条件
     *
     * @return
     */
    private String extractLimitWhere(String condition, String beginTime, String endTime, String category) {
        // 查询模糊条件
        String where = " 1=1";
        if (StringUtils.isNotEmpty(condition) || StringUtils.isNotEmpty(category)) {
            condition = condition.trim();
            if (StringUtils.isNotEmpty(category)) {
                where += " and category = #{category}";
            }
            where += " AND (" + ConditionUtil.like("product_id", condition);

            where += " OR " + ConditionUtil.like("product_name", condition);
            if ("已下架".equals(condition)) {
                where += " OR " + ConditionUtil.like("status", "2");
            }
            if ("未上架".equals(condition)) {
                where += " OR " + ConditionUtil.like("status", "0");
            }
            if ("销售中".equals(condition)) {
                where += " OR " + ConditionUtil.like("status", "1");
            }
            if ("直冲".equals(condition)) {
                where += " OR " + ConditionUtil.like("type", "0");
            }
            if ("卡密".equals(condition)) {
                where += " OR " + ConditionUtil.like("type", "1");
            }
            if ("链接".equals(condition)) {
                where += " OR " + ConditionUtil.like("type", "2");
            }
            where += " OR " + ConditionUtil.like("amount", condition);
            where += " OR " + ConditionUtil.like("remark", condition) + ")";
        }
        // 取两个日期之间或查询指定日期
        where = ConditionUtil.extractBetweenTime(beginTime, endTime, where, "add_time");
        return where.trim();
    }

    /**
     * 插入数据  2018年8月13日13:27:48
     *
     * @param param
     * @return
     */
    @Override
    public int insert(Product param) {
        if (param != null) {
            param.setAddTime(new Date());
            param.setEditTime(new Date());
            param.setStatus(0);
            try {
                IdWorker idWorker = IdWorker.getFlowIdWorkerInstance();
                param.setProductId(idWorker.nextId());
            } catch (Exception e) {

            }
            return productMapper.insert(param);
        }
        return -1;
    }

    /**
     * 更新数据  2018年8月13日13:28:01
     *
     * @param param
     * @return
     */
    @Override
    public int update(Product param) {
        param.setEditTime(new Date());
        return productMapper.update(param);
    }

    /**
     * 删除数据  2018年8月13日13:28:16
     *
     * @param param
     * @return
     */
    @Override
    public int delete(Product param) {
        return productMapper.deleteProduct(param.getProductId());
    }


    /**
     * 加载分页记录数 韦德 2018年8月30日11:29:22
     *
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer getLimitCount(Integer shopId, String condition, String beginTime, String endTime, String category) {
        condition = extractLimitWhere(condition, beginTime, endTime, category);
        return productMapper.getLimitCount(shopId, beginTime, endTime, condition, category);
    }

    /**
     * 加载总记录数 韦德 2018年8月30日11:29:11
     *
     * @return
     */
    @Override
    public Integer getCount(Integer shopId) {
        return productMapper.getCount(shopId);
    }


    @Override
    public Integer updateStatus(Long productId, Integer status) {
        return productMapper.updateStatus(productId, status);
    }


    /**
     * 根据Id查询商品 Timor 2019-2-22 10:47:38
     *
     * @param productId
     * @return
     */
    @Override
    public Product selectProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    /**
     * 根据分类已上架的有库存的商品信息 狗蛋 2019年2月28日15:45:41
     *
     * @return
     */
    @Override
    public List<ProductDetail> getProductByCategoryId(Integer categoryId) {
        List<Product> products = productMapper.getProductByCategoryId(categoryId);
        return filtrate(products);
    }

    /**
     * 获取所有的热推商品
     *
     * @return
     */
    @Override
    public List<ProductDetail> selectHotProducts() {
        List<Product> hotProducts = productMapper.selectHotProducts();
        return filtrate(hotProducts);
    }

    /**
     * 获取所有的热推商品 狗蛋 2019年3月1日17:28:44
     *
     * @return
     */
    @Override
    public List<ProductDetail> getShowProductList() {
        List<Product> productList = productMapper.getShowProductList();
        return filtrate(productList);
    }

    /**
     * 根据商品Id查询商品 钉子 2019年3月1日16:55:02
     *
     * @param productId
     * @return
     */
    @Override
    public ProductDetail selectProductByProductId(Long productId) {
        Product product=productMapper.selectById(productId);
        ProductDetail productDetail=new ProductDetail();
        BeanUtils.copyProperties(product,productDetail);
        Categorys categorys=categorysMapper.selectCategoryById(product.getCategory());
        productDetail.setCategoryName(categorys.getCategoryName());
        productDetail.setProductIdStr(product.getProductId().toString());
        return productDetail;
    }

    /**
     * 首页中根据商品类型查找商品 钉子 2019年3月11日11:42:32
     *
     * @return
     */
    @Override
    public List<ProductDetails> selectProductsByType() {
        List<ProductDetails> list=new ArrayList<>();
        ProductDetails productDetails=new ProductDetails();
            productDetails.setPage("fling");
            productDetails.setProductType("直冲");
            productDetails.setTypeId(0);
            List<Product> productList=productMapper.selectProductsByType(0);
            productDetails.setProductList(productList);
            list.add(productDetails);
        ProductDetails productDetails1=new ProductDetails();
            productDetails1.setPage("khamwi");
            productDetails1.setProductType("卡密");
            productDetails1.setTypeId(1);
            List<Product> productList1=productMapper.selectProductsByType(1);
            productDetails1.setProductList(productList1);
            list.add(productDetails1);
        ProductDetails productDetails2=new ProductDetails();
            productDetails2.setPage("link");
            productDetails2.setProductType("链接");
            productDetails2.setTypeId(2);
            List<Product> productList2=productMapper.selectProductsByType(2);
            productDetails2.setProductList(productList2);
            list.add(productDetails2);
        return list;
    }

    /**
     * 根据商品类型查找所有商品 钉子 2019年3月13日16:38:42
     *
     * @param type
     * @return
     */
    @Override
    public List<Product> selectProductsByOneType(Integer type) {
        return productMapper.selectProductsByOneType(type);
    }


    /**
     * 筛选所有可以展示的商品（有库存，或者是直冲）
     *
     * @param source
     * @return
     */
    private List<ProductDetail> filtrate(List<Product> source) {
        List<ProductDetail> newProducts = new ArrayList<>();
        source.forEach(item -> {
            ProductDetail productDetail = new ProductDetail();
            Categorys categorys=categorysMapper.selectCategoryById(item.getCategory());
            productDetail.setCategoryName(categorys.getCategoryName());
            if (item.getType().equals(1)) {
                // 卡密
                List<Khamwis> khamwis = khamwisMapper.selectKhamwiByProductId(item.getProductId());
                if (khamwis.size() > 0) {
                    BeanUtils.copyProperties(item,productDetail);
                    productDetail.setProductIdStr(item.getProductId().toString());
                    newProducts.add(productDetail);
                }
            } else if (item.getType().equals(2)) {
                // 链接
                List<Links> links = linksMapper.selectLinksByProductId(item.getProductId());
                if (links.size() > 0) {
                    BeanUtils.copyProperties(item,productDetail);
                    productDetail.setProductIdStr(item.getProductId().toString());
                    newProducts.add(productDetail);
                }
            } else {
                // 直冲
                BeanUtils.copyProperties(item,productDetail);
                productDetail.setProductIdStr(item.getProductId().toString());
                newProducts.add(productDetail);
            }
        });
        return newProducts;
    }


    /**
     * 获取商品信息根据编号（包含参数列表）
     *
     * @param productId
     * @return
     */
    @Override
    public ProductDetail selectProductDetailById(Long productId) {
        // 获取商品
        Product product = productMapper.selectById(productId);
        if(product == null) throw new MsgException("查询商品信息失败");
        ProductDetail productDetail = new ProductDetail();
        // 复制属性值
        BeanUtils.copyProperties(product,productDetail);
        productDetail.setProductIdStr(product.getProductId().toString());
        // 获取所有的关系
        List<ProductParameterRelation> productParameterRelations = parameterRelationMapper.selectByProductId(productId);

        if(productParameterRelations.size()>0) {
            List<ProductParameterDetail> list = new ArrayList<>();
            // 遍历所有的关系取出值
            productParameterRelations.forEach(item -> {
                // 关系详情
                ProductParameterDetail productParameterDetail = new ProductParameterDetail();
                //  查询关系名
                ProductParameter productParameter = parameterMapper.selectById(item.getProductParameterId());
                // 设置关系详情
                productParameterDetail.setName(productParameter.getName());
                productParameterDetail.setValue(item.getParameterValue());
                productParameterDetail.setId(item.getId());
                productParameterDetail.setProductParameterId(item.getProductParameterId());
                // 添加
                list.add(productParameterDetail);
            });
            // 设置关联参数
            productDetail.setParameterDetailList(list);
        }
        return productDetail;
    }

    /**
     * 商品发布
     *
     * @param product
     * @param valueList
     * @return
     */
    @Override
    @Transactional
    public Integer insertProduct(Product product, List<ProductParameterRelation> valueList) {
        if (product != null) {
            product.setAddTime(new Date());
            product.setEditTime(new Date());
            product.setStatus(0);
            try {
                IdWorker idWorker = IdWorker.getFlowIdWorkerInstance();
                product.setProductId(idWorker.nextId());
            } catch (Exception e) {

            }
            valueList.forEach(item->{
                item.setProductId(product.getProductId());
                parameterRelationMapper.insert(item);
            });

            return productMapper.insert(product);
        }
        return -1;
    }

    /**
     * 修改商品信息
     *
     * @param product
     * @param parameterRelationList
     */
    @Override
    @Transactional
    public Integer updateProduct(Product product, List<ProductParameterRelation> parameterRelationList) {

        parameterRelationList.forEach(item->{
            item.setProductId(product.getProductId());
            parameterRelationMapper.updateProductParameterRelationById(item);
        });
        return update(product);
    }
}
