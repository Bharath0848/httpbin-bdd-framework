package com.httpbin.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtility {

    private final String filePath = "./src/test/resources/testData/redirect_testdata.xlsx";

   public List<Map<String, String>> getSheetData(String sheetName) throws IOException {

    List<Map<String, String>> dataList = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(filePath);
         Workbook workbook = new XSSFWorkbook(fis)) {

        Sheet sheet = workbook.getSheet(sheetName);

        DataFormatter formatter = new DataFormatter();

        Row headerRow = sheet.getRow(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            Map<String, String> data = new HashMap<>();

            for (int j = 0; j < headerRow.getLastCellNum(); j++) {

                String key = formatter.formatCellValue(headerRow.getCell(j));
                String value = formatter.formatCellValue(row.getCell(j));

                data.put(key, value);
            }

            dataList.add(data);
        }
    }

    return dataList;
}
}


