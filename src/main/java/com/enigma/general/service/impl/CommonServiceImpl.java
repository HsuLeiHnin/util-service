package com.enigma.general.service.impl;

import com.enigma.general.service.CommonService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import lombok.extern.slf4j.Slf4j;
import org.rabbitconverter.rabbit.Rabbit;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j

public class CommonServiceImpl implements CommonService {
    @Override
    public InputStream htmlStringToPdf(String htmlString){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // For Myanmar Fonts
            String zawgyiHtmlString = Rabbit.uni2zg(htmlString);
            InputStream inputStreamUnicodeTtf = getClass().getResourceAsStream("/static/fonts/Zawgyi-One.ttf");
            byte[] bytesTtf = StreamUtil.inputStreamToArray(inputStreamUnicodeTtf);

            PdfFont pdfFont = PdfFontFactory.createFont(bytesTtf, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED, true);
            FontProvider fontProvider = new FontProvider();
            fontProvider.addFont(pdfFont.getFontProgram());

            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(fontProvider);


            HtmlConverter.convertToPdf(zawgyiHtmlString, pdfDoc, converterProperties);
            pdfDoc.close();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
