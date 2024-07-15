package com.enigma.general.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ExcelService {
    boolean hasExcelFormat(MultipartFile file);
    String getDataFromExcel(MultipartFile file);
    InputStream excelDownload(int size, int page);
}
