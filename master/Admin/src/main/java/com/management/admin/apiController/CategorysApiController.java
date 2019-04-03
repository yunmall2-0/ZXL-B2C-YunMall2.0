package com.management.admin.apiController;

import com.management.admin.biz.ICategorysService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.db.Categorys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "api/category")
public class CategorysApiController {
    @Autowired
    private ICategorysService iCategorysService;

    /**
     * 查询所有分类 钉子 2019年2月27日09:56:23
     * @return
     */
    @RequestMapping(value = "getAllCategorys")
    @ResponseBody
    public JsonArrayResult<Categorys> getAllCategorys(){
        List<Categorys> list=iCategorysService.selectAllCategorys();
        return new JsonArrayResult<Categorys>(list);
    }
    /**
     * 一对多查询所有一级和二级分类 钉子 2019年2月28日13:53:26
     * @return
     */
    @RequestMapping(value = "getAllCategorysAndTwo")
    @ResponseBody
    public JsonArrayResult<Categorys> getAllCategorysAndTwo(){
        List<Categorys> list=iCategorysService.selectAllOneCategorysAndTwo();
        return new JsonArrayResult<Categorys>(list);
    }
    /**
     * 查询所有二级分类 钉子 2019年2月26日17:33:27
     * @return
     */
    @RequestMapping(value = "getAllTwoCategory")
    @ResponseBody
    public JsonArrayResult<Categorys> GetAllTwoCategory(){
        List<Categorys> list=iCategorysService.selectAllTwoCategorys();
        return new JsonArrayResult<Categorys>(list);
    }

    /**
     * 查询所有一级分类 钉子 2019年2月27日09:56:23
     * @return
     */
    @RequestMapping(value = "getAllOneCategory")
    @ResponseBody
    public JsonArrayResult<Categorys> getAllOneCategory(){
        List<Categorys> list=iCategorysService.selectAllOneCategorys();
        return new JsonArrayResult<Categorys>(list);
    }

}
