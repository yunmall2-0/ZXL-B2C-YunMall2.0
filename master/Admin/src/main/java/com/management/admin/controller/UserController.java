package com.management.admin.controller;

import com.management.admin.biz.impl.UserServiceImpl;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.db.Users;
import com.management.admin.utils.JsonUtil;
import com.management.admin.utils.web.SessionUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("users")
public class UserController extends BaseController{

    @Autowired
    private UserServiceImpl userService;

    /**
     * 跳转到用户列表界面 Timor 2019-2-23 10:04:13
     * @param request
     * @param model
     * @return
     */
    @GetMapping(value = "mumberlist")
    public String userList(HttpServletRequest request, Model model){
        Integer userId=SessionUtil.getSession(request).getUserId();
        Users user=userService.getUserById(userId);
        model.addAttribute("user",user);
        return "user/index";
    }


    /**
     * 展示所有用户 Timor 2019-2-23 10:03:43
     * @param page
     * @param limit
     * @param condition
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "showUsers")
    @ResponseBody
    public JsonArrayResult<Orders> showAllOrders(Integer page, String limit, String condition, String beginTime, String endTime){
        Integer count=0;
        List<Users> userLimit = userService.getUsersLimit(page, limit, condition, beginTime, endTime);
        JsonArrayResult jsonArrayResult = new JsonArrayResult(0, userLimit);
        count = userService.getLimitCount(condition,beginTime, endTime);
        jsonArrayResult.setCount(count);
        return jsonArrayResult;
    }

    /**
     * 修改用户状态 Timor 2019-2-23 10:03:14
     * @param state
     * @param userId
     * @return
     */
    @GetMapping(value = "updateDisable")
    @ResponseBody
    public JsonResult<Users> updateDisable(Integer state,Integer userId){
        Integer result=userService.updateDisable(state,userId);
        if (result>0){
            return JsonResult.successful();
        }else {
            return JsonResult.failing();
        }
    }

    /**
     * 跳转到用户编辑界面 Timor  2019-2-23 10:57:34
     * @param userId
     * @param model
     * @return
     */
    @GetMapping(value = "editUser")
    public String editUser(Integer userId,Model model){
        Users users=userService.getUserById(userId);
        model.addAttribute("users",users);
        return "user/edit";
    }

    /**
     * 修改用户信息  Timor 2019-2-23 11:34:49
     * @param users
     * @return
     */
    @PostMapping(value = "editUsers")
    @ResponseBody
    public JsonResult<Users> updateUsers(Users users){
        Integer result=userService.updateUsersById(users);
        if(result>0){
            return JsonResult.successful();
        }
        return JsonResult.failing();
    }

    /**
     * 跳转新增用户界面 Timor 2019-2-23 15:20:29
     * @return
     */
    @GetMapping("insertuser")
    public String newusers(){
        return "user/newusers";
    }


   /**
     * 新增用户 Timor 2019-2-23 16:16:10
     * @param user
     * @return
     */
    @PostMapping(value = "newusers")
    @ResponseBody
    public JsonResult<Users> insertUsers(Users user){
        Integer result=userService.insertUsers(user);
        if(result==-2){
            return new JsonResult<>().failing("已存在");
        }else if(result==-1){
            return JsonResult.failing();
        }
        return JsonResult.successful();
    }

}
