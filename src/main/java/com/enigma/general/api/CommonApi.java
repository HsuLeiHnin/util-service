package com.enigma.general.api;

import com.enigma.general.payload.request.HtmlPdfReqBody;
import com.enigma.general.service.CommonService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.layout.font.FontProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.rabbitconverter.rabbit.Rabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/")
@Slf4j

public class CommonApi {
    @Autowired
    private CommonService commonService;

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
}
