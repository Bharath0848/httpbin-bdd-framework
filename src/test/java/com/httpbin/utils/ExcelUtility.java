package com.httpbin.utils;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtility {

    // UPDATED: Path matches your screenshot (src/test/resources/testData/Exceldata.xlsx)
    private final String filePath = System.getProperty("user.dir") + "/src/test/resources/testData/Exceldata.xlsx";

    public String getCellDataByKey(String sheetName, String key, int valueColumn) throws IOException {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row != null && row.getCell(0) != null) {
                    String cellKey = formatter.formatCellValue(row.getCell(0));

                    if (key.equalsIgnoreCase(cellKey)) {
                        if (row.getCell(valueColumn) != null) {
                            return formatter.formatCellValue(row.getCell(valueColumn));
                        } else {
                            return null;
                        }
                    }
                }
            }
            throw new RuntimeException("Key not found in sheet: " + key);
        }
    }
}