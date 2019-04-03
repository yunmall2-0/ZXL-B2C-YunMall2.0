package com.management.admin.repository;

import com.management.admin.entity.db.Links;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LinksMapper extends MyMapper<Links>{
    /**
     * 根据商品ID分页查找链接  钉子 2019年2月21日09:44:51
     * @param productId
     * @param page
     * @param limit
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select * from links where product_id=#{productId} and is_delete=0 and ${condition} ORDER BY add_time DESC LIMIT #{page},${limit}")
    List<Links> selectAllLinksByProductId(@Param("productId") Long productId,@Param("page") Integer page, @Param("limit") String limit
            , @Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);

    /**
     * 查询分页记录数 钉子 2019年2月21日10:04:30
     * @param productId
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select count(link_id) from links where product_id=#{productId} and ${condition} and is_delete=0 ")
    Integer selectLimitCount(@Param("productId") Long productId,@Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);

    /**
     * 查询总记录数  钉子 2019年2月21日10:04:48
     * @param productId
     * @return
     */
    @Select("select count(*) from links where product_id=#{productId} and is_delete=0")
    Integer selectLinksCount(Long productId);

    /**
     * 添加链接 钉子 2019年2月21日09:51:57
     * @param links
     * @return
     */
    @Insert("insert into links(link,product_id,add_time) " +
            "values(#{link},#{productId},#{addTime})")
    Integer insertLinks(Links links);

    /**
     * 链接使用完修改状态 钉子 2019年2月21日10:02:07
     * @param linkId
     * @return
     */
    @Update("update links set status=1,get_time=NOW() where link_id=#{linkId}")
    Integer updateLinks(Integer linkId);

    /**
     * 删除链接  钉子 2019年2月21日10:02:02
     * @param linkId
     * @return
     */
    @Delete("update links set is_delete=1 where link_id=#{linkId}")
    Integer deleteLinks(Integer linkId);

    /**
     * 查询一条未使用的链接  钉子 2019年2月22日09:51:08
     * @return
     */
    @Select("select * from links where product_id=#{productId} and is_delete=0 and status=0 ORDER BY add_time ASC LIMIT 1")
    Links selectOneLink(Long productId);

    /**
     * 根据ID查询链接 钉子 2019年2月22日10:47:24
     * @param linkId
     * @return
     */
    @Select("select * from links where link_id=#{linkId} and is_delete=0")
    Links selectOneById(Integer linkId);
    /**
     * 根据商品编号查询所有未使用的链接 狗蛋 2019年2月22日09:52:32
     * @param productId
     * @return
     */
    @Select("select * from links where product_id = #{productId} and is_delete=0 and status=0")
    List<Links> selectLinksByProductId(Long productId);


}
