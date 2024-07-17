package com.enigma.service.impl;

import com.enigma.service.CommonService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.pdfcleanup.PdfCleaner;
import com.itextpdf.pdfcleanup.autosweep.CompositeCleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;
import lombok.extern.slf4j.Slf4j;
import org.rabbitconverter.rabbit.Rabbit;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public InputStream pdfFieldMapper(){
        try {
            Map<String, String> replacementTexts = Map.of("", "");

            InputStream inputStreamPyiDaungSuTtf = getClass().getResourceAsStream("/static/fonts/Pyidaungsu-253Regular.ttf");
            byte[] bytesTtf = StreamUtil.inputStreamToArray(inputStreamPyiDaungSuTtf);
            PdfFont pyidaungsuFont = PdfFontFactory.createFont(bytesTtf, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED, true);


            InputStream inputStreamPdfTemplate = getClass().getResourceAsStream("/template/new.pdf");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            PdfReader reader = new PdfReader(inputStreamPdfTemplate);
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            Map<List<IPdfTextLocation>, Map.Entry<String, String>> locationList = new HashMap<>();
            for (Map.Entry<String, String> entry : replacementTexts.entrySet()) {

                String pdfKey = entry.getKey();

                CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
                strategy.add(new RegexBasedCleanupStrategy(pdfKey).setRedactionColor(ColorConstants.WHITE));

                PdfCleaner.autoSweepCleanUp(pdfDoc, strategy);

                locationList.put((List<IPdfTextLocation>) strategy.getResultantLocations(), entry);
                log.info("Finished CleanUp");
            }

            for (Map.Entry<List<IPdfTextLocation>, Map.Entry<String, String>> entryLocation : locationList.entrySet()) {
                for (IPdfTextLocation location : entryLocation.getKey()) {
                    log.info("Start to replace text....");
                    PdfPage page = pdfDoc.getPage(location.getPageNumber() + 1);
                    PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), page.getDocument());
                    Canvas canvas = new Canvas(pdfCanvas, location.getRectangle().setWidth(1000));
                    canvas.add(new Paragraph(Rabbit.uni2zg(entryLocation.getValue().getValue())).setFont(pyidaungsuFont).setFontSize(11).setMarginTop(-3f));

                    log.info("Finished replace text....");
                }
            }

            pdfDoc.close();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
