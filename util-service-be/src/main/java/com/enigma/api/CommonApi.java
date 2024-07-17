package com.enigma.api;

import com.enigma.constant.ErrorConstant;
import com.enigma.payload.request.HtmlPdfReqBody;
import com.enigma.service.CommonService;
import com.enigma.service.ExcelService;
import com.enigma.service.ResponseHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(path = "/")
@Slf4j

public class CommonApi {
    @Autowired
    private CommonService commonService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ResponseHandlerService responseHandlerService;

    @RequestMapping(value = "/html-pdf", method = RequestMethod.GET)
    public void htmlStringToPdf(@RequestBody HtmlPdfReqBody htmlPdfReqBody,
                                  HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("Request - API for HTML to PDF");
        try {
            InputStream inputStream = commonService.htmlStringToPdf(htmlPdfReqBody.getHtmlString());

            httpServletResponse.setContentType(MediaType.APPLICATION_PDF.toString());
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "HTML-PDF");
            IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/pdf-template", method = RequestMethod.GET)
    public void pdfFieldMapper(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("Request - API for PDF Field Mapping");
        try {
            InputStream inputStream = commonService.pdfFieldMapper();

            httpServletResponse.setContentType(MediaType.APPLICATION_PDF.toString());
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "HTML-PDF");
            IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        log.info("Request - API for Excel Upload");
        try {
            String responseText = "";
            if (excelService.hasExcelFormat(file)) {
                responseText = excelService.getDataFromExcel(file);
            }
            return responseHandlerService.commonResHandler(responseText);
        }catch (Exception e){
            log.error("Error in uploadFile : {}", file.getName());
            return responseHandlerService.commonResHandler(ErrorConstant.ERR_00);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> getFile(@RequestParam(name = "size", defaultValue = "10")int size,
                                            @RequestParam(name = "page", defaultValue = "0")int page) throws IOException {
        log.info("Request - API for Excel Download");
        try {
            String filename = "Test.xlsx";
            InputStreamResource file = new InputStreamResource(excelService.excelDownload(size,page));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file);
        }catch (Exception e){
            log.error("Error in getFile");
            return responseHandlerService.commonResHandler(ErrorConstant.ERR_00);
        }
    }
}
