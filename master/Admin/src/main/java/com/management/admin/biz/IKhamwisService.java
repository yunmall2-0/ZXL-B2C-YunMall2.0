package com.management.admin.biz;

import com.management.admin.entity.db.Khamwis;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IKhamwisService {
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
    List<Khamwis> selectKhamwisByProductId(Long productId,Integer page, String limit, String condition, String beginTime, String endTime);

    /**
     * 查询分页记录数 钉子 2019年2月20日10:04:30
     * @param productId
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    Integer selectLimitCount(Long productId,String condition, String beginTime, String endTime);

    /**
     * 查询总记录数  钉子 2019年2月20日10:04:48
     * @param productId
     * @return
     */
    Integer selectKhamwisCount(Long productId);

    /**
     * 添加卡密  钉子 2019年2月20日10:10:45
     * @param khamwis
     * @return
     */
    Integer insertKhamwis(Khamwis khamwis);
    /**
     * 批量添加卡密  钉子 2019年2月20日10:10:45
     * @param
     * @return
     */
    Integer insertManyKhamwis(String productId, MultipartFile file);

    /**
     * 卡密使用后修改状态  钉子 2019年2月20日10:16:33
     * @param cardId
     * @return
     */
    Integer updateKhamwis(Integer cardId);

    /**
     * 删除卡密 钉子 2019年2月20日10:17:47
     * @param cardId
     * @return
     */
    Integer deleteKhamwis(Integer cardId);

    /**
     * 批量删除卡密 钉子 2019年2月21日14:54:06
     * @param strings
     * @return
     */
    Integer deleteManyKhamwis(String[] strings);

    /**
     * 查询一条未使用的卡密 钉子 2019年2月21日13:19:01
     * @return
     */
    Khamwis selectKhamwisOne(Long productId);

    /**
     * 根据ID查询卡密 钉子 2019年2月22日10:45:07
     * @param cardId
     * @return
     */
    Khamwis selectOneById(Integer cardId);


}
