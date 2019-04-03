/***
 * @pName management
 * @name HomeController
 * @user HongWei
 * @date 2018/8/27
 * @desc
 */
package com.management.admin.controller;

import com.management.admin.annotaion.WebLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("management")
public class HomeController {

    @GetMapping("index")
    public String index()
    {
        return "home/index";
    }
}
