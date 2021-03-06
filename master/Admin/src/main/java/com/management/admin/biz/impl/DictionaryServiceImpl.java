/***
 * @pName management
 * @name DictionaryServiceImpl
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.biz.impl;

import com.management.admin.biz.IDictionaryService;
import com.management.admin.biz.IProductService;
import com.management.admin.entity.DataDictionary;
import com.management.admin.entity.DynamicConfiguration;
import com.management.admin.entity.db.Dictionary;
import com.management.admin.entity.db.Product;
import com.management.admin.entity.resp.GroupInformation;
import com.management.admin.entity.resp.Runner;
import com.management.admin.exception.InfoException;
import com.management.admin.exception.MsgException;
import com.management.admin.repository.DictionaryMapper;
import com.management.admin.repository.ProductMapper;
import com.management.admin.utils.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl extends BaseServiceImpl<Dictionary> implements IDictionaryService {
    private final DictionaryMapper dictionaryMapper;
    @Autowired
    private DynamicConfiguration dynamicConfiguration;

    private ProductMapper productMapper;

    @Autowired
    public DictionaryServiceImpl(DictionaryMapper dictionaryMapper, ProductMapper productMapper) {
        this.dictionaryMapper = dictionaryMapper;
        this.productMapper = productMapper;
    }

    /**
     * 获取全部数据 韦德 2018年8月13日13:26:57
     *
     * @return
     */
    @Override
    public List<Dictionary> getList() {
        return dictionaryMapper.selectAll();
    }

    /**
     * 获取聚合广告信息 韦德 2018年8月18日13:11:46
     *
     * @return
     */
    @Override
    public GroupInformation getGroupInformation() {
        GroupInformation groupInformation = new GroupInformation();
        List<Dictionary> list = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("home.group.info")).collect(Collectors.toList());
        List<String> runnerAds = list.stream().filter(d -> d.getKey().contains("top.runner"))
                .map(d -> d.getValue()).collect(Collectors.toList());
        groupInformation.setTopRunnerAds(runnerAds);

        String textAd = list.stream().filter(d -> d.getKey().contains("home.group.info.top.text-ad")).findFirst().get().getValue();
        groupInformation.setTopTextAd(textAd);

        String leftAdTitle = list.stream().filter(d -> d.getKey().contains("home.group.info.center.left-ad.title")).findFirst().get().getValue();
        groupInformation.setCenterLeftAdTitle(leftAdTitle);

        String leftAdDesc = list.stream().filter(d -> d.getKey().contains("home.group.info.center.left-ad.desc")).findFirst().get().getValue();
        groupInformation.setCenterLeftAdDesc(leftAdDesc);

        String rightAdTitle = list.stream().filter(d -> d.getKey().contains("home.group.info.center.right-ad.title")).findFirst().get().getValue();
        groupInformation.setCenterRightAdTitle(rightAdTitle);

        String rightAdDesc = list.stream().filter(d -> d.getKey().contains("home.group.info.center.right-ad.desc")).findFirst().get().getValue();
        groupInformation.setCenterRightAdDesc(rightAdDesc);

        String rightAdButton = list.stream().filter(d -> d.getKey().contains("home.group.info.center.right-ad.button")).findFirst().get().getValue();
        groupInformation.setCenterRightAdButton(rightAdButton);

        String bottomAdTitle = list.stream().filter(d -> d.getKey().contains("home.group.info.center.bottom-ad.title")).findFirst().get().getValue();
        groupInformation.setCenterBottomAdTitle(bottomAdTitle);

        String bottomAdDesc = list.stream().filter(d -> d.getKey().contains("home.group.info.center.bottom-ad.desc")).findFirst().get().getValue();
        groupInformation.setCenterBottomAdDesc(bottomAdDesc);

        String bottomQcode = list.stream().filter(d -> d.getKey().contains("home.group.info.center.bottom-ad.qcode")).findFirst().get().getValue();
        groupInformation.setCenterBottomAdQCode(bottomQcode);

        String consumeServiceHtml = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("my.consume.service.html")).findFirst().get().getValue();
        groupInformation.setConsumeServiceHtml(consumeServiceHtml);

        String payQCodeUrl = dynamicConfiguration.getDomain() + "/images/upload/" + DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("finance.pays.qrcode.image")).findFirst().get().getValue();
        groupInformation.setPayQRCodeUrl(payQCodeUrl);

        String payCode = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("finance.pays.qrcode.payCode")).findFirst().get().getValue();
        groupInformation.setPayCode(payCode);

        String giveAmount = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("finance.give.amount")).findFirst().get().getValue();
        groupInformation.setGiveAmount(giveAmount);

        String version = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("version")).findFirst().get().getValue();
        groupInformation.setVersion(version);

        String iosDownload = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("ios.download")).findFirst().get().getValue();
        groupInformation.setIosDownload(iosDownload);

        String androidDownload = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("android.download")).findFirst().get().getValue();
        groupInformation.setAndroidDownload(androidDownload);

        String regPackagePrice = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("invite.regPackagePrice")).findFirst().get().getValue();
        groupInformation.setRegPackagePrice(regPackagePrice);

        String playAwardPrice = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("invite.playAwardPrice")).findFirst().get().getValue();
        groupInformation.setPlayAwardPrice(playAwardPrice);

        String joinCount = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("invite.joinCount")).findFirst().get().getValue();
        groupInformation.setJoinCount(joinCount);

        String appMarquee = DataDictionary.DATA_DICTIONARY.values().stream().filter(d -> d.getKey().contains("app.marquee")).findFirst().get().getValue();
        groupInformation.setAppMarquee(appMarquee);
        return groupInformation;
    }

    /**
     * 上传二维码 韦德 2018年8月31日13:31:29
     *
     * @param url
     */
    @Override
    @Transactional
    public void updateQRCode(String url) {
        Dictionary dictionary = DataDictionary.DATA_DICTIONARY.get("finance.pays.qrcode.image");
        int count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), url);
        if (count == 0) throw new MsgException("更新二维码失败");

        dictionary = DataDictionary.DATA_DICTIONARY.get("finance.pays.qrcode.payCode");
        String absPath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        String path = absPath.substring(1, absPath.length()) + "static/images/upload/" + url;
        String payCode = null;
        try {
            payCode = QRCodeUtil.decode(path);
        } catch (Exception e) {
            System.err.println(e);
            throw new InfoException("解析二维码失败");
        }
        payCode = payCode.substring(22, payCode.length());
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), payCode);
        if (count == 0) throw new MsgException("更新二维条码失败");
    }

    /**
     * 刷新本地字典 韦德 2018年8月31日13:46:02
     */
    @Override
    public void refresh() {
        List<Dictionary> list = this.getList();
        if (list != null && !list.isEmpty()) {
            List<Dictionary> dictionaryList = list;
            DataDictionary.DATA_DICTIONARY.clear();
            Map<String, Dictionary> dataDictionaryList = new HashMap<>();
            dictionaryList.forEach(dictionary -> dataDictionaryList.put(dictionary.getKey(), dictionary));
            DataDictionary.DATA_DICTIONARY = dataDictionaryList;
        }
    }

    /**
     * 更新联系方式 韦德 2018年9月2日01:44:08
     *
     * @param html
     * @param appMarquee
     */
    @Override
    @Transactional
    public void updateConfiguration(String html, String giveAmount, String version, String iosDownload, String androidDownload, String regPackagePrice, String playAwardPrice, String joinCount, String appMarquee) {
        Dictionary dictionary = DataDictionary.DATA_DICTIONARY.get("my.consume.service.html");
        int count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), html);
        if (count == 0) throw new MsgException("更新失败[A01]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("finance.give.amount");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), giveAmount);
        if (count == 0) throw new MsgException("更新失败[A02]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("version");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), version);
        if (count == 0) throw new MsgException("更新失败[A03]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("ios.download");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), iosDownload);
        if (count == 0) throw new MsgException("更新失败[A04]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("android.download");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), androidDownload);
        if (count == 0) throw new MsgException("更新失败[A05]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("invite.regPackagePrice");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), regPackagePrice);
        if (count == 0) throw new MsgException("更新失败[A06]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("invite.playAwardPrice");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), playAwardPrice);
        if (count == 0) throw new MsgException("更新失败[A07]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("invite.joinCount");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), joinCount);
        if (count == 0) throw new MsgException("更新失败[A08]");

        dictionary = DataDictionary.DATA_DICTIONARY.get("app.marquee");
        count = 0;
        count = dictionaryMapper.updateUrlById(dictionary.getDictionaryId(), appMarquee);
        if (count == 0) throw new MsgException("更新失败[A09]");
    }

    @Override
    public Integer upadteConfig(String key, String value) {
        return dictionaryMapper.updateUrlByKey(key, value);
    }

    /**
     * 获取网站首页轮播图 狗蛋 2019年3月1日14:40:44
     */
    @Override
    public List<Runner> getWebsiteHomePageRunner() {
        List<Runner> websiteHomeRunner = new ArrayList<>();
        List<Dictionary> dictionaries = dictionaryMapper.selectByKey("website.homepage.runner");
        for (int i = 0; i < dictionaries.size(); i++) {
            int j = i + 1;
            Runner runner = new Runner();
            String imageKey = "website.homepage.runner" + j + ".image";
            runner.setImageKey(imageKey);
            if (dictionaries.get(i).getKey().equals(imageKey)) {
                runner.setImage(dictionaries.get(i).getValue());
                String eq = "website.homepage.runner" + j + ".product";
                String s = dictionaries.stream().filter(yuan -> yuan.getKey().equals(eq)).findFirst().get().getValue();
                ;
                runner.setProductKey(eq);
                runner.setProductIdStr(s);
                Product product = productMapper.selectById(Long.parseLong(s));
                if (product != null) runner.setProductName(product.getProductName());
                websiteHomeRunner.add(runner);
            }
        }
        return websiteHomeRunner;
    }
}

