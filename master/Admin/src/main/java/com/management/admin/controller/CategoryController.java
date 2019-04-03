package com.management.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.admin.biz.ICategorysService;
import com.management.admin.entity.JsonArrayResult;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Categorys;
import com.management.admin.entity.resp.CategoryWrapper;
import com.management.admin.entity.resp.TreeWrappage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "categorys")
public class CategoryController {
    @Autowired
    private ICategorysService iCategorysService;

    /**
     * 跳转分类管理页面 钉子 2019年2月25日13:55:02
     * @return
     */
    @RequestMapping(value = "getAllCategoryPage")
    public String getAllCategoryPage(){
        return "categorys/categorys";
    }

    /**
     * 跳转分类添加页 钉子 2019年2月25日13:56:18
     * @return
     */
    @RequestMapping(value = "addCategoryPage")
    public String addCategoryPage(){
        return "categorys/addCategory";
    }
    /**
     * 跳转编辑分类添加页 钉子 2019年2月25日13:56:18
     * @return
     */
    @RequestMapping(value = "editCategoryPage")
    public String editCategoryPage(){
        return "categorys/editCategory";
    }
    /**
     * 查询所有分类 钉子 2019年2月25日13:35:42
     * @return
     */
    @RequestMapping(value = "getAllCategorys")
    @ResponseBody
    public JsonArrayResult<CategoryWrapper> getAllCategorys(){
        List<Categorys> list=iCategorysService.selectAllCategorys();
        List<CategoryWrapper> categoryExts = new ArrayList<>();
        for (Categorys backCategory : list) {
            CategoryWrapper backCategoryExt = new CategoryWrapper(backCategory.getCategoryId(),
                    backCategory.getCategoryName(), backCategory.getParentId(),backCategory.getSort(),backCategory.getIsLeaf());
            categoryExts.add(backCategoryExt);
        }
        return new JsonArrayResult<CategoryWrapper>(categoryExts);
    }

    /**
     * 模糊查询分类 钉子 2019年2月25日13:48:57
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "getAllCategorysByDim")
    @ResponseBody
    public JsonArrayResult<Categorys> getAllCategorysByDim(String categoryName){
        List<Categorys> list=iCategorysService.selectAllCategoryByName(categoryName);
        return new JsonArrayResult<Categorys>().dataTable(list);
    }
    /**
     * 查询一级分类 钉子 2019年2月25日13:48:57
     * @return
     */
    @RequestMapping(value = "getAllOneCategorys")
    @ResponseBody
    public JsonArrayResult<Categorys> getAllOneCategorys(){
        List<Categorys> list=iCategorysService.selectAllOneCategorys();
        return new JsonArrayResult<Categorys>(list);
    }
    /**
     * 根据ID查询分类 钉子 2019年2月25日13:48:57
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "getCategorysById")
    @ResponseBody
    public JsonResult getCategorysById(Integer categoryId){
        Categorys categorys=iCategorysService.selectCategoryById(categoryId);
        return new JsonResult(200,categorys);
    }

    /**
     * 添加分类 钉子 2019年2月25日15:53:09
     * @param request
     * @return
     */
    @RequestMapping(value = "insertCategory")
    @ResponseBody
    public JsonResult insertCategory(HttpServletRequest request){
        String str=request.getParameter("categorys");
        ObjectMapper mapper=new ObjectMapper();
        try {
            Categorys categorys=mapper.readValue(str,Categorys.class);
            int i=iCategorysService.insertCategory(categorys);
            if (i>0){
                return JsonResult.successful();
            }else{
                return JsonResult.failing();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.failing();
        }
    }
    /**
     * 修改分类 钉子 2019年2月25日15:53:09
     * @param categoryName
     * @param sort
     * @return
     */
    @RequestMapping(value = "updateCategory")
    @ResponseBody
    public JsonResult updateCategory(Integer categoryId,String categoryName,Integer sort){
        Categorys categorys=new Categorys();
        categorys.setCategoryId(categoryId);
        categorys.setCategoryName(categoryName);
        categorys.setSort(sort);
        int i=iCategorysService.updateCategory(categorys);
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }
    /**
     * 删除分类 钉子 2019年2月25日15:53:09
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "deleteCategory")
    @ResponseBody
    public JsonResult deleteCategory(Integer categoryId){
        Categorys categorys=iCategorysService.selectCategoryById(categoryId);
        int i=0;
        if (categorys.getParentId().equals(0)){
            i = iCategorysService.deleteCategoryAndTwo(categoryId);
        }else {
            i = iCategorysService.deleteCategory(categoryId);
        }
        if (i>0){
            return JsonResult.successful();
        }else{
            return JsonResult.failing();
        }
    }
    @GetMapping("getTreeWrapper")
    @ResponseBody
    public JsonResult getTreeWrapper(){
        List<Categorys> categorys = iCategorysService.selectAllCategorys();
        List<TreeWrappage> treeWrappages = iCategorysService.convertTree(categorys, 0);
        return new JsonResult(200,treeWrappages);
    }
}
