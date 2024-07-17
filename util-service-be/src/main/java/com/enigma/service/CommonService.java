package com.enigma.service;
import java.io.InputStream;

public interface CommonService {
    InputStream htmlStringToPdf(String htmlString);
    InputStream pdfFieldMapper();
}
