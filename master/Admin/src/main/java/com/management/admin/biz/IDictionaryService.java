/***
 * @pName management
 * @name DictionaryService
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.biz;

import com.management.admin.entity.db.Dictionary;
import com.management.admin.entity.resp.GroupInformation;
import com.management.admin.entity.resp.Runner;

import java.util.List;


public interface IDictionaryService extends IBaseService<Dictionary> {

    /**
     * 获取聚合广告信息 韦德 2018年8月18日13:11:46
     * @return
     */
    GroupInformation getGroupInformation();

    /**
     * 上传二维码 韦德 2018年8月31日13:31:29
     * @param url
     */
    void updateQRCode(String url);

    /**
     * 刷新本地字典 韦德 2018年8月31日13:46:02
     */
    void refresh();

    /**
     * 更新基础配置信息 韦德 2018年9月2日01:44:08
     * @param html
     * @param appMarquee
     */
    void updateConfiguration(String html, String give, String version, String iosDownload, String androidDownload, String regPackagePrice, String playAwardPrice, String joinCount, String appMarquee);

    /**
     *根据key修改value
     */
    Integer upadteConfig(String key,String value);


    /**
     * 获取网站首页轮播图 狗蛋 2019年3月1日14:40:44
     */
    List<Runner> getWebsiteHomePageRunner();
}
