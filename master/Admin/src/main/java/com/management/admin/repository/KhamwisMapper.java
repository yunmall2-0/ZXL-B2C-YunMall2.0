package com.management.admin.repository;

import com.management.admin.entity.db.Khamwis;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KhamwisMapper extends MyMapper<Khamwis>{
    /**
     * 根据商品ID查找卡密 钉子  2019年2月20日09:56:36
     * @param productId
     * @param page
     * @param limit
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select * from khamwis where product_id=#{productId} and is_delete=0 and ${condition} ORDER BY add_time DESC LIMIT #{page},${limit}")
    List<Khamwis> selectKhamwisByProductId(@Param("productId") Long productId,@Param("page") Integer page, @Param("limit") String limit
            , @Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);

    /**
     * 查询分页记录数 钉子 2019年2月20日10:04:30
     * @param productId
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    @Select("select count(card_id) from khamwis where product_id=#{productId} and ${condition} and is_delete=0 ")
    Integer selectLimitCount(@Param("productId") Long productId,@Param("beginTime") String beginTime
            , @Param("endTime") String endTime
            , @Param("condition") String condition);

    /**
     * 查询总记录数  钉子 2019年2月20日10:04:48
     * @param productId
     * @return
     */
    @Select("select count(*) from khamwis where product_id=#{productId} and is_delete=0")
    Integer selectKhamwisCount(Long productId);

    /**
     * 添加卡密  钉子 2019年2月20日10:10:45
     * @param khamwis
     * @return
     */
    @Insert("insert into khamwis (card_number,card_pwd,product_id,add_time) " +
            "values (#{cardNumber},#{cardPwd},#{productId},#{addTime})")
    Integer insertKhamwis(Khamwis khamwis);

    /**
     * 卡密使用后修改状态  钉子 2019年2月20日10:16:33
     * @param cardId
     * @return
     */
    @Update("update khamwis set status=1,get_time=NOW() where card_id=#{cardId}")
    Integer updateKhamwis(Integer cardId);

    /**
     * 删除卡密 钉子 2019年2月20日10:17:47
     * @param cardId
     * @return
     */
    @Update("update khamwis set is_delete=1 where card_id=#{cardId}")
    Integer deleteKhamwis(Integer cardId);

    /**
     * 查询一条未使用的卡密 钉子 2019年2月21日13:18:17
     * @return
     */
    @Select("select * from khamwis where product_id=#{productId} and is_delete=0 and status=0 ORDER BY add_time ASC LIMIT 1")
    Khamwis selectKhamwisOne(Long productId);

    /**
     * 根据ID查询卡密  钉子 2019年2月22日10:44:31
     * @param cardId
     * @return
     */
    @Select("select * from khamwis where card_id=#{cardId} and is_delete=0")
    Khamwis selectOneById(Integer cardId);

    /**
     * 根据商品查询所有未使用的卡密 狗蛋 2019年2月22日09:52:21
     * @return
     */
    @Select("select * from khamwis " +
            "where product_id =#{productId}" +
            " and is_delete=0 and status=0")
    List<Khamwis> selectKhamwiByProductId(Long productId);
}
