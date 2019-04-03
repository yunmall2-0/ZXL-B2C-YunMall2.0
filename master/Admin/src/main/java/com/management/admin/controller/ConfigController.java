package com.management.admin.controller;

import com.management.admin.biz.IDictionaryService;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.resp.Runner;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private IDictionaryService dictionaryService;

    /**
     * 跳转网站首页banner设置 狗蛋 2019年3月1日16:31:56
     * @param model
     * @return
     */
    @GetMapping("getWebsiteHomePageRunner")
    public String getWebsiteHomePageRunner(final Model model) {
        List<Runner> websiteHomePageRunner = dictionaryService.getWebsiteHomePageRunner();
        model.addAttribute("runner",websiteHomePageRunner);
        return "config/index";
    }

    /**
     * 编辑轮播图 狗蛋 2019年3月1日17:04:23
     * @param ikey
     * @return
     */
    @PostMapping("getDetails")
    public String getDetails(String ikey,final Model model){
        List<Runner> websiteHomePageRunner = dictionaryService.getWebsiteHomePageRunner();
        Runner runner = websiteHomePageRunner.stream().filter(item -> item.getImageKey().equals(ikey)).findFirst().get();

        model.addAttribute("runner",runner);
        return "config/compile";
    }

    /**
     * 修改配置信息 狗蛋 2019年3月2日09:54:52
     * @param runnerStr
     * @return
     */
    @GetMapping("edit")
    @ResponseBody
    public JsonResult edit(String runnerStr){
        if(StringUtils.isBlank(runnerStr)) throw new MsgException("修改信息为空！");
        Runner model = JsonUtil.getModel(runnerStr, Runner.class);
        Integer result1 = dictionaryService.upadteConfig(model.getImageKey(), model.getImage());
        Integer result2 = dictionaryService.upadteConfig(model.getProductKey(),model.getProductIdStr());
        if(result1<=0||result2<=0) throw new MsgException("修改失败！");
        return new JsonResult(200,"success");
    }
}
