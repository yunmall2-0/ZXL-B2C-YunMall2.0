package com.management.admin.apiController;

import com.management.admin.biz.IDictionaryService;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.resp.Runner;
import com.management.admin.exception.MsgException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/home")
public class HomeApiController {

    @Autowired
    private IDictionaryService dictionaryService;

    /**
     * 获取网站首页轮播图信息 狗蛋 2019年3月2日10:15:28
     * @return
     */
    @GetMapping("getHomeCarousel")
    public JsonResult getHomeCarousel(){
        List<Runner> homePageRunner = dictionaryService.getWebsiteHomePageRunner();
        if(homePageRunner.size()<=0) throw new MsgException("获取信息失败！");
        return new JsonResult(200,homePageRunner);
    }
}
