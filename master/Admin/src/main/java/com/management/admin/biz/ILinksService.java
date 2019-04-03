package com.management.admin.biz;

import com.management.admin.entity.db.Links;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ILinksService {
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
    List<Links> selectAllLinksByProductId(Long productId,Integer page, String limit, String condition, String beginTime, String endTime);

    /**
     * 查询分页记录数 钉子 2019年2月21日10:04:30
     * @param productId
     * @param beginTime
     * @param endTime
     * @param condition
     * @return
     */
    Integer selectLimitCount(Long productId,String condition, String beginTime, String endTime);

    /**
     * 查询总记录数  钉子 2019年2月21日10:04:48
     * @param productId
     * @return
     */
    Integer selectLinksCount(Long productId);

    /**
     * 添加卡密 钉子 2019年2月21日09:51:57
     * @param links
     * @return
     */
    Integer insertLinks(Links links);
    /**
     * 批量添加链接  钉子 2019年2月21日10:18:57
     * @param
     * @return
     */
    Integer insertManyLinks(String productId, MultipartFile file);

    /**
     * 链接使用完修改状态 钉子 2019年2月21日10:02:07
     * @param linkId
     * @return
     */
    Integer updateLinks(Integer linkId);

    /**
     * 删除链接  钉子 2019年2月21日10:02:02
     * @param linkId
     * @return
     */
    Integer deleteLinks(Integer linkId);

    /**
     * 删除多个链接 钉子 2019年2月21日15:03:38
     * @param strings
     * @return
     */
    Integer deleteManyLinks(String[] strings);

    /**
     * 查询一条未使用的链接  钉子 2019年2月22日09:53:12
     * @return
     */
    Links selectOneLink(Long productId);

    /**
     * 根据ID查询链接  钉子 2019年2月22日10:48:04
     * @param linkId
     * @return
     */
    Links selectOneById(Integer linkId);
}
