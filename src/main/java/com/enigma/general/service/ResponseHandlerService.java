package com.enigma.general.service;

import com.enigma.general.payload.response.CommonResBody;
import org.springframework.http.ResponseEntity;

public interface ResponseHandlerService {
    ResponseEntity<?> commonResHandler(String code);
}
