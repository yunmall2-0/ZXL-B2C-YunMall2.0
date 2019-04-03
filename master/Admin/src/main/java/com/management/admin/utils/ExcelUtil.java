package com.management.admin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import javax.xml.bind.ValidationException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author lks
 * @2017年2月27日
 * @下午3:02:53
 */
public class ExcelUtil
{
    public static final String EXCEL_XLS = ".xls";
    public static final String EXCEL_XLSX=".xlsx";
    public static final String FILE_SPLIT =".";
    /**
     * 选择其中的方法
     * @param path
     * @return
     * @throws Exception
     */
    public static List<List<String>> changeExcelType(String path) throws Exception
    {
        int lastIndexOf = path.lastIndexOf(ExcelUtil.FILE_SPLIT);
        String excelType = path.substring(lastIndexOf);
        List<List<String>> list = null;
        if(EXCEL_XLS.equalsIgnoreCase(excelType)){
            list = readXls(path);
        }else if(EXCEL_XLSX.equalsIgnoreCase(excelType)){
            list = readXlsx(path);
        }else{
            throw new ValidationException("上传的类型不存在！");
        }
        return list;
    }
    /**
     * 创建读取Excel的文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<List<String>> readXls(String path) throws Exception {
        InputStream in = new FileInputStream(new File(path));
        HSSFWorkbook workbook = new HSSFWorkbook(in);
        List<List<String>> result = new ArrayList<List<String>>();
        int sheetsNum = workbook.getNumberOfSheets() -1;
        //遍历sheet
        for (int i = 0; i <= sheetsNum; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            int lastRowNum = sheet.getLastRowNum();
            int firstRowNum = sheet.getFirstRowNum();
            //遍历行
            for (int j = firstRowNum ; j <= lastRowNum; j++) {
                List<String> rowlist = new ArrayList<String>();
                HSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();
                //遍历单元格
                for (int k = firstCellNum; k <= lastCellNum; k++) {
                    HSSFCell cell = row.getCell(k);
                    if (cell == null) {
                        continue;
                    }
                    rowlist.add(readCell(cell));
                }
                result.add(rowlist);
            }
        }
        return result;
    }

    /**
     * 读取excel的文件的尾部为xlsx
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<List<String>> readXlsx(String path) throws Exception {
        check(path);
        InputStream in = new FileInputStream(new File(path));
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        List<List<String>> result = new ArrayList<List<String>>();
        int sheetsNum = workbook.getNumberOfSheets()-1;
        for (int i = 0; i <= sheetsNum; i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            //遍历行
            for (int j = firstRowNum; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();
                List<String> rowResult = new ArrayList<>();
                for (int k = firstCellNum; k <= lastCellNum; k++) {
                    XSSFCell cell = row.getCell(k);
                    if (cell == null) {
                        continue;
                    }
                    rowResult.add(readCell(cell));
                }
                result.add(rowResult);
            }
        }
        return result;
    }

    /**
     * 读取单元格中的数据
     *
     * @param cell
     * @return
     */
    //判断其中的excel中的cell中的数字可能是不同的类型
    public static String readCell(Cell cell)
    {
        String cellValue = "";
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String temp = cell.getStringCellValue();
                //当是double时
//                if(temp.contains(".")){
//                    Double d = new Double(temp);
//                    cellValue = String.valueOf(d);
//                }else{
                    cellValue = temp.trim();
//                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
//                if(cellValue.indexOf(".") > -1){
////                    if(!NumberUtils.isNumber(cellValue)){
////                        cellValue = cellValue.trim();
////                    }
//                    Double d = new Double(cellValue);
//                    cellValue = String.valueOf(d);
//                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                try {
                    cellValue = String.valueOf(cell.getStringCellValue());
                } catch (IllegalFormatException e) {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                if(cellValue != null){
                    cellValue = cellValue.replaceAll("#N/A","").trim();
                }
                break;
            default:
                cellValue =  "";
                break;
        }
        return cellValue;
    }

    /**
     * 验证
     *
     * @param path
     * @throws FileNotFoundException
     */
    public static void check(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在！");
        }
    }

//    public static void main(String[] args) {
//        String path = "F:\\测试.xlsx";
//        List<List<String>> lists = null;
//        try
//        {
//            lists = changeExcelType(path);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        for (int i = 0; i < lists.size(); i++)
//        {
//            List<String> list = lists.get(i);
//            for (int j = 0; j < list.size(); j++)
//            {
//                System.out.println(list.get(j)+","+j+","+i);
//
//            }
//        }
//    }

}

