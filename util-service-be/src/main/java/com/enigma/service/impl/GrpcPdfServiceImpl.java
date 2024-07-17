package com.enigma.service.impl;

import com.enigma.PdfRequest;
import com.enigma.PdfResponse;
import com.enigma.PdfServiceGrpc;
import com.enigma.service.CommonService;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@GRpcService
public class GrpcPdfServiceImpl extends PdfServiceGrpc.PdfServiceImplBase {
    @Autowired
    private CommonService commonService;

    @Override
    public void htmlStringToPdf(PdfRequest pdfRequest, StreamObserver<PdfResponse> responseObserver){
        log.info("Start - gRPC Request for PDF");
        try {
            InputStream pdfStream = commonService.htmlStringToPdf(pdfRequest.getHtmlString());
            ByteString response = ByteString.copyFrom(pdfStream.readAllBytes());

            responseObserver.onNext(PdfResponse.newBuilder().setPdfByte(response).build());
            responseObserver.onCompleted();
        }catch (IOException ex){
            throw new RuntimeException("Error in gRPC htmlToPdf");
        }
    }
}
