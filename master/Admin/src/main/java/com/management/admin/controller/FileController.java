package com.management.admin.controller;

import com.google.common.base.Joiner;
import com.management.admin.entity.Constant;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.resp.FileUploadResp;
import com.management.admin.exception.InfoException;
import com.management.admin.exception.MsgException;
import com.management.admin.utils.*;
import com.management.admin.exception.InfoException;
import com.management.admin.utils.FileUtil;
import com.management.admin.utils.http.CosUtil;
import com.management.admin.utils.http.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


@Controller
@RequestMapping(value = "management/file")
public class FileController {

    private static final String PREFIX = "file";

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 实现文件上传
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public void fileUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        //文件不存在
        if (file == null || file.isEmpty()) {
            throw new InfoException("文件不存在");
        }
        //后缀名检测
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        //判断后缀名
        String[] fileTypes = Constant.FILE_TYPES;
        boolean flag = false;
        for (String fileType : fileTypes) {
            if (fileType.equals(extension)){
                flag = true;
                break;
            }
        }

        if (!flag){
            //不能上传
            throw new InfoException("只能上传" + Joiner.on(",").join(Constant.FILE_TYPES) + "格式的图片");
        }

        try {
            //文件类型检测
            String mimeType = FileUtil.getFileMimeType(file.getBytes());

            flag = false;
            for (String mt : Constant.MIME_TYPES) {
                if (mt.equals(mimeType)){
                    flag = true;
                    break;
                }
            }
            System.out.println(mimeType);

            if (!flag){
                //不能上传
                throw new InfoException("只能上传" + Joiner.on(",").join(Constant.MIME_TYPES) + "类型的图片");
            }

            //可以上传
            //动态生成图片名称 名称=日期毫秒+3位随机数字
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String format = dateFormat.format(new Date());


            //生成随机数
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                format += random.nextInt(10);
            }

            //获取跟目录
            File abspath = new File(ResourceUtils.getURL("classpath:").getPath());
            if (!abspath.exists())
                abspath = new File("");

            //如果上传目录为/static/images/upload/，则可以如下获取：
            File upload = new File(abspath.getAbsolutePath(), "/static/images/upload/");
            if (!upload.exists())
                upload.mkdirs();

            //保存的路径
            String path = upload.getAbsoluteFile().getAbsolutePath();

            //最后的文件名
            String finalFileName = format + "." + extension;

            File dest = new File(path + "/" + finalFileName);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                dest.getParentFile().mkdir();
            }


            //保存文件
            file.transferTo(dest);

            //前台img访问的地址  直接使用 img:src 显示
            String url = finalFileName;

            //返回保存文件的地址
            ResponseUtil.renderJson(response, new JsonResult().successful(url));

        } catch (IllegalStateException e) {
            ResponseUtil.renderJson(response, JsonResult.failing());
        } catch (Exception e) {
            ResponseUtil.renderJson(response, JsonResult.failing());
        }
    }

    @RequestMapping(value = "cosUpload", method = RequestMethod.POST)
    @ResponseBody
    public void cosUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url= CosUtil.upload(file);
        ResponseUtil.renderJson(response, new JsonResult().successful(url));

    }

    @RequestMapping(value = "cosUploadImg", method = RequestMethod.POST)
    @ResponseBody
    public FileUploadResp cosUploadImg(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url= CosUtil.upload(file);
        return new FileUploadResp(url);

    }

    @RequestMapping(value = "cosUploadFile", method = RequestMethod.POST)
    @ResponseBody
    public void cosUploadFile(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {

        redisTemplate.opsForValue().set("123","456");
        Object o = redisTemplate.opsForValue().get("123");
        System.out.println(o);

        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        Long uuid = UUID.next();
        // 用uuid作为文件名，防止生成的临时文件重复
        final File excelFile = File.createTempFile(uuid.toString(), prefix);
        // MultipartFile to File
        file.transferTo(excelFile);
        ReadExcel.readExcel(excelFile);
        //你的业务逻辑

        //程序结束时，删除临时文件
        deleteFile(excelFile);

    }

    /**
     * 删除
     *
     * @param files
     */
    private void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
