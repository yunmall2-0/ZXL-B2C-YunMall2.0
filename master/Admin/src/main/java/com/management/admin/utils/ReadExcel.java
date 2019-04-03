package com.management.admin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadExcel {

    public static void main(String[] args) {

    }

    public static void readExcel(File excel) throws IOException {

        if(!excel.isFile()){
            return;
        }
        //解析excel

        Workbook hb = null;

        System.out.println(excel.getName());

        String[] split = excel.getName().split("\\.");
        System.out.println(split[1]);
        //用流的方式先读取到你想要的excel的文件
        FileInputStream fis=new FileInputStream(excel);

        //根据文件后缀（xls/xlsx）进行判断
        if ( "xls".equals(split[1])){

            hb = new HSSFWorkbook(fis);
        }else if ("xlsx".equals(split[1])){
            hb = new XSSFWorkbook(fis);
        }else {
            System.out.println("文件类型错误!");
            return;
        }
        //获取第一个表单sheet
        Sheet sheet=hb.getSheetAt(0);
        //获取第一行
        int firstrow=    sheet.getFirstRowNum();
        //获取最后一行
        int lastrow=    sheet.getLastRowNum();
        //循环行数依次获取列数
        for (int i = firstrow; i < lastrow+1; i++) {
            //获取哪一行i
            Row row=sheet.getRow(i);
            if (row!=null) {
                //获取这一行的第一列
                int firstcell=row.getFirstCellNum();
                //获取这一行的最后一列
                int lastcell=row.getLastCellNum();
                //创建一个集合，用处将每一行的每一列数据都存入集合中
                List<String> list=new ArrayList<>();
                for (int j = firstcell; j <lastcell; j++) {
                    //获取第j列
                    Cell cell=row.getCell(j);

                    if (cell!=null) {
                        list.add(cell.toString());
                    }
                }
                if (list.size()>0) {
                    System.out.println(list.get(0));
                    System.out.println(list.get(1));
                }
            }
        }
        fis.close();
    }

}
