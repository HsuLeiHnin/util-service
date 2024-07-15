package com.enigma.general.service.impl;

import com.enigma.general.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.enigma.general.constant.ErrorConstant.SUCCESS;

@Service
public class ExcelServiceImpl implements ExcelService {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String[] HEADERS = {"", ""};

    @Override
    public boolean hasExcelFormat(MultipartFile file) {
        if(!TYPE.equals(file.getContentType())){
            return false;
        }
        return true;
    }

    @Override
    public String getDataFromExcel(MultipartFile file){
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            boolean skipHeader = true;

            for (Row currentRow : sheet) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                List<Cell> cells = new ArrayList<>();

                int lastCellIndex = Math.max(currentRow.getLastCellNum(), 3);
                int currentCellIndex = 0;

                while(currentCellIndex<lastCellIndex){
                    Cell currentCell = currentRow.getCell(currentCellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    cells.add(currentCell);
                    currentCellIndex++;
                }

                validateBlankCell(cells);
            }

            return SUCCESS;
        }catch (IOException ex){
            throw new RuntimeException("Error in createExcelSheet : ".concat(ex.getMessage()));
        }
    }

    @Override
    public InputStream excelDownload(int size, int page){
        Pageable pageable = ((size == -1) && (page == -1)) ? Pageable.unpaged() : PageRequest.of(page, size);
//        Data you want to export like an excel sheet [from db]
        InputStream createExcel = createExcelSheet(List.of());
        return createExcel;
    }

    private InputStream createExcelSheet(List<?> data) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("");
            Row headerRow = sheet.createRow(0);

            //Header Font Style and Cell Style
            Font headerFont =workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            //Define Number Format
            CellStyle cellStyleNumber = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            cellStyleNumber.setDataFormat(format.getFormat("#,##0.00"));
            cellStyleNumber.setAlignment(HorizontalAlignment.RIGHT);

            //Define Text Format
            CellStyle cellStyleText = workbook.createCellStyle();
            cellStyleText.setDataFormat(format.getFormat("@"));
            cellStyleText.setAlignment(HorizontalAlignment.CENTER);

            //Define Date Format
            CellStyle cellStyleDate = workbook.createCellStyle();
            cellStyleDate.setDataFormat(format.getFormat("dd/mm/yyyy"));
            cellStyleDate.setAlignment(HorizontalAlignment.CENTER);

            for(int col=0; col<HEADERS.length; col++){
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for(Object info : data){
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(info.toString());
                row.createCell(1).setCellValue(info.toString());
                row.createCell(2).setCellValue(info.toString());

                row.getCell(0).setCellStyle(cellStyleText);
                row.getCell(1).setCellStyle(cellStyleNumber);
                row.getCell(2).setCellStyle(cellStyleDate);
            }

            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 20 * 256);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }catch (IOException ex){
          throw new RuntimeException("Error in createExcelSheet : ".concat(ex.getMessage()));
        }
    }

    private List<Cell> validateBlankCell(List<Cell> cells){
        for(Cell cell : cells) {
            if (cell == null) {
                cell.setCellValue("-");
            }
        }
       return cells;
    }
}
