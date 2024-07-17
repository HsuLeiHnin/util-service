package com.enigma.service;

import org.springframework.http.ResponseEntity;

public interface ResponseHandlerService {
    ResponseEntity<?> commonResHandler(String code);
}
