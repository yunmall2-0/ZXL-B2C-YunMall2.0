package com.management.admin.biz.impl;

import com.management.admin.biz.IKhamwisService;
import com.management.admin.entity.db.Khamwis;
import com.management.admin.repository.KhamwisMapper;
import com.management.admin.repository.utils.ConditionUtil;
import com.management.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.Date;
import java.util.List;
@Service
public class KhamwisServiceImpl implements IKhamwisService {
    private final KhamwisMapper khamwisMapper;
    @Autowired
    public KhamwisServiceImpl(KhamwisMapper khamwisMapper) {
        this.khamwisMapper = khamwisMapper;
    }

    /**
     * 分页查询商品的卡密 钉子 2019年2月20日10:22:30
     * @param productId
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<Khamwis> selectKhamwisByProductId(Long productId, Integer page, String limit, String condition, String beginTime, String endTime) {
        page= ConditionUtil.extractPageIndex(page,limit);
        String where=extractLimitWhere(condition, beginTime, endTime);
        List<Khamwis> list=khamwisMapper.selectKhamwisByProductId(productId,page, limit, beginTime, endTime, where);
        return list;
    }

    /**
     * 分页查询记录数 钉子 2019年2月20日10:22:46
     * @param productId
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Integer selectLimitCount(Long productId, String condition, String beginTime, String endTime) {
        String where = extractLimitWhere(condition,beginTime, endTime);
        return khamwisMapper.selectLimitCount(productId,beginTime, endTime, where);
    }

    /**
     * 查询总记录数  钉子 2019年2月20日10:23:00
     * @param productId
     * @return
     */
    @Override
    public Integer selectKhamwisCount(Long productId) {
        return khamwisMapper.selectKhamwisCount(productId);
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
            where += " AND (" + ConditionUtil.like("card_id", condition, false, "");
            where += " OR " + ConditionUtil.like("card_number", condition, false, "")+ ")";
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
     * 添加卡密  钉子 2019年2月20日10:23:09
     * @param khamwis
     * @return
     */
    @Override
    public Integer insertKhamwis(Khamwis khamwis) {
        return khamwisMapper.insertKhamwis(khamwis);
    }

    /**
     * 批量添加卡密 钉子 2019年2月20日10:50:46
     * @param
     * @return
     */
    @Override
    @Transactional
    public Integer insertManyKhamwis(String productId, MultipartFile file) {
        File file1=new File("C:\\bucket.xlsx");
        Khamwis khamwis=new Khamwis();
        int a=0;
        int b=0;

        try {
            file.transferTo(file1);
            List<List<String>> lists = null;
            lists = ExcelUtil.changeExcelType("C:\\bucket.xlsx");
            b=lists.size();
            for (int i = 0; i < lists.size(); i++)
            {
                List<String> list = lists.get(i);
                for (int j = 0; j < list.size(); j++)
                {
                    if (j%2==0){
                        khamwis.setCardNumber(list.get(j));
                    }else{
                        khamwis.setCardPwd(list.get(j));
                    }
                }
                khamwis.setProductId(Long.valueOf(productId));
                khamwis.setAddTime(new Date());
                a += khamwisMapper.insertKhamwis(khamwis);
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
     * 卡密使用后修改状态  钉子 2019年2月20日10:23:31
     * @param cardId
     * @return
     */
    @Override
    public Integer updateKhamwis(Integer cardId) {
        return khamwisMapper.updateKhamwis(cardId);
    }

    /**
     * 删除卡密 钉子 2019年2月20日10:23:39
     * @param cardId
     * @return
     */
    @Override
    public Integer deleteKhamwis(Integer cardId) {
        return khamwisMapper.deleteKhamwis(cardId);
    }

    /**
     * 批量删除卡密 钉子 2019年2月21日14:54:36
     * @param strings
     * @return
     */
    @Override
    public Integer deleteManyKhamwis(String[] strings) {
        int a=0;
        for (int i=0;i<strings.length;i++){
            a += khamwisMapper.deleteKhamwis(Integer.parseInt(strings[i]));
        }
        if (a==strings.length){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 查询一条未使用的卡密 钉子 2019年2月21日13:19:30
     * @return
     */
    @Override
    public Khamwis selectKhamwisOne(Long productId) {
        return khamwisMapper.selectKhamwisOne(productId);
    }

    /**
     * 根据id查询卡密 钉子 2019年2月22日10:45:39
     * @param cardId
     * @return
     */
    @Override
    public Khamwis selectOneById(Integer cardId) {
        return khamwisMapper.selectOneById(cardId);
    }
}
