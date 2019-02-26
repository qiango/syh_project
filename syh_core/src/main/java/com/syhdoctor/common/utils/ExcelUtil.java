package com.syhdoctor.common.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {

    private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * @param filename
     * @param startx     开始x
     * @param starty     开始y
     * @param maxColumn  最大列
     * @param sheetCount 表数量
     * @return
     */
    public static List<Map<String, Object>> readExcel(String filename, int startx, int starty, int maxColumn, int sheetCount) {
        try {
            if (StrUtil.isEmpty(filename)) {
                return null;
            }
            if (filename.endsWith(".xls")) {
                return readXls(filename, startx, starty, maxColumn, sheetCount);
            } else if (filename.endsWith(".xlsx")) {
                return readXlsx(filename, startx, starty, maxColumn, sheetCount);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("ExcelUtil readExcel exception %s", e.getMessage()));
            return null;
        }
    }

    private static List<Map<String, Object>> readXlsx(String filename, int startx, int starty, int maxColumn, int sheetCount) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            InputStream is = new FileInputStream(filename);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

            // Read the Sheet
            for (int numSheet = 0; numSheet < (sheetCount == 0 ? xssfWorkbook.getNumberOfSheets() : sheetCount); numSheet++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                if (xssfSheet == null) {
                    continue;
                }// Read the Row
                for (int rowNum = starty; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                    if (xssfRow != null) {
                        Map<String, Object> temp = new HashMap<>();
                        for (int colNum = startx; colNum < maxColumn; colNum++) {
                            Cell cell = xssfRow.getCell(colNum);
                            boolean isMerge = false;
                            if (cell != null) {
                                isMerge = isMergedRegion(xssfSheet, rowNum, cell.getColumnIndex());
                            }
                            //判断是否具有合并单元格
                            String value;
                            if (isMerge) {
                                value = getMergedRegionValue(xssfSheet, xssfRow.getRowNum(), cell.getColumnIndex());
                            } else {
                                value = getCellValue(cell);
                            }
                            temp.put(colNum + "", value);
                        }
                        result.add(temp);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("ExcelUtil readXls exception %s", e.getMessage()));
            return null;
        }
    }

    private static List<Map<String, Object>> readXls(String filename, int startx, int starty, int maxColumn, int sheetCount) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            InputStream is = new FileInputStream(filename);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            // Read the Sheet
            for (int numSheet = 0; numSheet < (sheetCount == 0 ? hssfWorkbook.getNumberOfSheets() : sheetCount); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                // Read the Row
                for (int rowNum = starty; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow != null) {
                        Map<String, Object> temp = new HashMap<>();
                        for (int colNum = startx; colNum < maxColumn; colNum++) {
//                            LogUtil.info(log,"excel :" + rowNum + "|" + colNum);
                            boolean isMerge = false;
                            Cell cell = hssfRow.getCell(colNum);
                            if (cell != null) {
                                isMerge = isMergedRegion(hssfSheet, rowNum, cell.getColumnIndex());
                            }
                            //判断是否具有合并单元格
                            String value;
                            if (isMerge) {
                                value = getMergedRegionValue(hssfSheet, hssfRow.getRowNum(), cell.getColumnIndex());
                            } else {
                                value = getCellValue(cell);
                            }
                            temp.put(colNum + "", value);
                        }
                        result.add(temp);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("ExcelUtil readXls exception %s", e.getMessage()));
            return null;
        }
    }

    /**
     * 判断指定的单元格是否是合并单元格
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格的值
     */
    private static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        return null;
    }

    /**
     * 获取单元格的值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellTypeEnum() == CellType.STRING) {
            String url = cell.getHyperlink() == null ? null : cell.getHyperlink().getAddress();
            if (StrUtil.isEmpty(url)) {
                return cell.getStringCellValue();
            } else {
                return url;
            }
        } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
            return cell.getCellFormula();
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    public static void createExcel(String[] keys, List<Map<String, Object>> datas, String fileName) {
        if (FileUtil.validateFile(fileName)) {
            FileUtil.delFile(fileName);
        }
        FileUtil.createFile(fileName);
        FileOutputStream os;
        final int MAX_LEN = 60000;
        try {
            os = new FileOutputStream(fileName);
            // 创建Excel的工作书册 Workbook,对应到一个excel文档
            HSSFWorkbook wb = new HSSFWorkbook();

            int size = datas.size() / MAX_LEN;
            for (int i = 0; i < size; i++) {
                // 创建Excel的工作sheet,对应到一个excel文档的tab
                HSSFSheet sheet = wb.createSheet("sheet" + (i + 1));
                for (int j = 0; j < MAX_LEN; j++) {
                    Map<String, Object> model = datas.get(i * MAX_LEN + j);
                    setCell(keys, model, sheet, j);
                }
            }
            int tempSize = datas.size() % MAX_LEN;
            if (tempSize > 0) {
                HSSFSheet sheet = wb.createSheet("sheet" + (size + 1));
                for (int z = 0; z < tempSize; z++) {
                    Map<String, Object> model = datas.get(size * MAX_LEN + z);
                    setCell(keys, model, sheet, z);
                }
            }
            wb.write(os);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createExcelNotKey(long time, List<Map<String, Object>> datas, String fileName) {
        if (FileUtil.validateFile(fileName)) {
            FileUtil.delFile(fileName);
        }
        FileUtil.createFile(fileName);
        FileOutputStream os;
        final int MAX_LEN = 60000;
        try {
            os = new FileOutputStream(fileName);
            // 创建Excel的工作书册 Workbook,对应到一个excel文档
            HSSFWorkbook wb = new HSSFWorkbook();

            int size = datas.size() / MAX_LEN;
            for (int i = 0; i < size; i++) {
                // 创建Excel的工作sheet,对应到一个excel文档的tab
                HSSFSheet sheet = wb.createSheet("sheet" + (i + 1));
                for (int j = 0; j < MAX_LEN; j++) {
                    Map<String, Object> model = datas.get(i * MAX_LEN + j);
                    setCellNew(time, model, sheet, j);
                }
            }
            int tempSize = datas.size() % MAX_LEN;
            if (tempSize > 0) {
                HSSFSheet sheet = wb.createSheet("sheet" + (size + 1));
                for (int z = 0; z < tempSize; z++) {
                    Map<String, Object> model = datas.get(size * MAX_LEN + z);
                    setCellNew(time, model, sheet, z);
                }
            }
            wb.write(os);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createExcelNotKeyNew(List<Map<String, Object>> datas, String fileName) {
        if (FileUtil.validateFile(fileName)) {
            FileUtil.delFile(fileName);
        }
        FileUtil.createFile(fileName);
        FileOutputStream os;
        final int MAX_LEN = 60000;
        try {
            os = new FileOutputStream(fileName);
            // 创建Excel的工作书册 Workbook,对应到一个excel文档
            HSSFWorkbook wb = new HSSFWorkbook();

            int size = datas.size() / MAX_LEN;
            for (int i = 0; i < size; i++) {
                // 创建Excel的工作sheet,对应到一个excel文档的tab
                HSSFSheet sheet = wb.createSheet("sheet" + (i + 1));
                sheet.setDefaultColumnWidth(10);
                for (int j = 0; j < MAX_LEN; j++) {
                    Map<String, Object> model = datas.get(i * MAX_LEN + j);
                    setCell(model, sheet, j);
                }
            }
            int tempSize = datas.size() % MAX_LEN;
            if (tempSize > 0) {
                HSSFSheet sheet = wb.createSheet("sheet" + (size + 1));
                sheet.setDefaultColumnWidth(10);
                for (int z = 0; z < tempSize; z++) {
                    Map<String, Object> model = datas.get(size * MAX_LEN + z);
                    setCell(model, sheet, z);
                }
            }
            wb.write(os);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("pclass", 1);
        map.put("class", 1);
        map.put("content", 1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("pclass", 1);
        map2.put("class", 1);
        map2.put("content", 1);
        result.add(map);
        result.add(map2);

        ExcelUtil.createExcel(new String[]{"pclass", "class", "content"}, result, "D:/abbott_local_temp.xlsx");*/
        List<Map<String, Object>> maps = readExcel("/home/qwq/桌面/leyuangyun.xlsx", 0, 0, 9, 1);
    }

    private static void setCell(String[] keys, Map<String, Object> model, HSSFSheet sheet, int index) {
        // 创建Excel的sheet的一行
        HSSFRow row = sheet.createRow(index);
        // 创建一个Excel的单元格
        for (int i = 0; i < keys.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(ModelUtil.getStr(model, keys[i]));
        }
    }

    private static void setCellNew(long times, Map<String, Object> model, HSSFSheet sheet, int index) {
        // 创建Excel的sheet的一行
        HSSFRow row = sheet.createRow(index);
        if (index == 0) {
            String doctorname = ModelUtil.getStr(model, "doctorname");
            String time = ModelUtil.getStr(model, "time");
            String nowTime = ModelUtil.getStr(model, "nowtime");
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(doctorname);
            HSSFCell cells = row.createCell(1);
            cells.setCellValue(time);
            HSSFCell cellNow = row.createCell(2);
            cellNow.setCellValue(nowTime);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = sdf.format(times);
            // 创建一个Excel的单元格
            String name = ModelUtil.getStr(model, "name");
            List<Map<String, Object>> time = (List<Map<String, Object>>) ModelUtil.getList(model, "timeList", new ArrayList<>());
            String alltime = ModelUtil.getStr(model, "alltime");
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(name);
            HSSFCell cells = row.createCell(1);
            cells.setCellValue(alltime);
            HSSFCell cellNow = row.createCell(2);
            cellNow.setCellValue(str);
            for (int i = 0; i < time.size(); i++) {
                HSSFCell cellss = row.createCell(i + 3);
                cellss.setCellValue(time.get(i).get("visitingstarttime").toString());
            }
        }
    }

    private static void setCell(Map<String, Object> model, HSSFSheet sheet, int index) {
        // 创建Excel的sheet的一行
        HSSFRow row = sheet.createRow(index);
        if (index == 0) {
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(ModelUtil.getStr(model, "docname"));
            HSSFCell cells = row.createCell(1);
            cells.setCellValue(ModelUtil.getStr(model, "alltime"));
            HSSFCell cellNow = row.createCell(2);
            cellNow.setCellValue(ModelUtil.getStr(model, "date"));
        } else {
            // 创建一个Excel的单元格
            List<Map<String, Object>> time = (List<Map<String, Object>>) ModelUtil.getList(model, "timeList", new ArrayList<>());
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(ModelUtil.getStr(time.get(0), "name"));
            HSSFCell cells = row.createCell(1);
            HSSFCell cellNow = row.createCell(2);
            cellNow.setCellValue(ModelUtil.getStr(time.get(0), "day"));
            long all = 0;
            for (int i = 0; i < time.size(); i++) {
                all += ModelUtil.getLong(time.get(i), "time") / 60000;
                HSSFCell cellss = row.createCell(i + 3);
                cellss.setCellValue(String.format("%s-%s", ModelUtil.getStr(time.get(i), "visitingstarttime"), ModelUtil.getStr(time.get(i), "visitingendtime")));
            }
            long m = all;
            long h = 0;
            if (all > 60) {
                m = all % 60;
                h = all / 60;
            }
            String result = String.format("%s小时%s分钟", h, m);
            cells.setCellValue(String.valueOf(result));
        }
    }
}
