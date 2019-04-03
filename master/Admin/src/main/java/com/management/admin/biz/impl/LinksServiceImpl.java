package com.management.admin.biz.impl;

import com.management.admin.biz.ILinksService;
import com.management.admin.entity.db.Links;
import com.management.admin.repository.LinksMapper;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
@Service
public class LinksServiceImpl implements ILinksService {
    private final LinksMapper linksMapper;
    @Autowired
    public LinksServiceImpl(LinksMapper linksMapper) {
        this.linksMapper = linksMapper;
    }

    /**
     * 根据商品ID分页查询链接 钉子 2019年2月21日10:13:45
     * @param productId
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<Links> selectAllLinksByProductId(Long productId, Integer page, String limit, String condition, String beginTime, String endTime) {
        page=ConditionUtil.extractPageIndex(page,limit);
        String where=extractLimitWhere(condition,beginTime,endTime);
        List<Links> list=linksMapper.selectAllLinksByProductId(productId,page,limit,beginTime,endTime,where);
        return list;
    }

    /**
     * 分页查询记录数 钉子 2019年2月21日10:14:22
     * @param productId
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer selectLimitCount(Long productId, String condition, String beginTime, String endTime) {
        String where=extractLimitWhere(condition,beginTime,endTime);
        return linksMapper.selectLimitCount(productId,beginTime,endTime,where);
    }

    /**
     * 查询总记录数 钉子 2019年2月21日10:15:33
     * @param productId
     * @return
     */
    @Override
    public Integer selectLinksCount(Long productId) {
        return linksMapper.selectLinksCount(productId);
    }
    /**
     * 提取分页条件
     * @return
     */
    private String extractLimitWhere(String condition,String beginTime, String endTime) {
        // 查询模糊条件
        String where = " 1=1";
        if(condition != null) {
            condition = condition.trim();
            where += " AND (" + ConditionUtil.like("link_id", condition, false, "");
            where += " OR " + ConditionUtil.like("link", condition, false, "")+ ")";
        }
        // 取两个日期之间或查询指定日期
        where = extractBetweenTime(beginTime, endTime, where);
        return where.trim();
    }
    /**
     * 提取两个日期之间的条件
     * @return
     */
    private String extractBetweenTime(String beginTime, String endTime, String where) {
        if ((beginTime != null && beginTime.contains("-")) &&
                endTime != null && endTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (beginTime != null && beginTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }else if (endTime != null && endTime.contains("-")){
            where += " AND add_time BETWEEN #{beginTime} AND #{endTime}";
        }
        return where;
    }

    /**
     * 添加链接 钉子 2019年2月21日10:16:24
     * @param links
     * @return
     */
    @Override
    public Integer insertLinks(Links links) {
        return linksMapper.insertLinks(links);
    }

    /**
     * 批量添加链接  钉子 2019年2月21日11:24:46
     * @param productId
     * @param file
     * @return
     */
    @Override
    public Integer insertManyLinks(String productId, MultipartFile file) {
        File file1=new File("C:\\bucketLink.xlsx");
        Links links=new Links();
        int a=0;
        int b=0;

        try {
            file.transferTo(file1);
            List<List<String>> lists = null;
            lists = ExcelUtil.changeExcelType("C:\\bucketLink.xlsx");
            b=lists.size();
            for (int i = 0; i < lists.size(); i++)
            {
                List<String> list = lists.get(i);
                for (int j = 0; j < list.size(); j++)
                {
                    links.setLink(list.get(i));
                    links.setProductId(Long.valueOf(productId));
                    links.setAddTime(new Date());
                    a += linksMapper.insertLinks(links);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (a==b){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 修改链接状态 钉子 2019年2月21日10:17:11
     * @param linkId
     * @return
     */
    @Override
    public Integer updateLinks(Integer linkId) {
        return linksMapper.updateLinks(linkId);
    }

    /**
     * 删除链接 钉子 2019年2月21日10:17:52
     * @param linkId
     * @return
     */
    @Override
    public Integer deleteLinks(Integer linkId) {
        return linksMapper.deleteLinks(linkId);
    }

    /**
     * 删除多个链接 钉子 2019年2月21日15:04:03
     * @param strings
     * @return
     */
    @Override
    public Integer deleteManyLinks(String[] strings) {
        int a=0;
        for (int i=0;i<strings.length;i++){
            a += linksMapper.deleteLinks(Integer.parseInt(strings[i]));
        }
        if (a==strings.length){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 查询一条未使用的链接 钉子 2019年2月22日09:56:30
     * @param productId
     * @return
     */
    @Override
    public Links selectOneLink(Long productId) {
        return linksMapper.selectOneLink(productId);
    }

    /**
     * 根据ID查询链接  钉子 2019年2月22日10:48:25
     * @param linkId
     * @return
     */
    @Override
    public Links selectOneById(Integer linkId) {
        return linksMapper.selectOneById(linkId);
    }
}
