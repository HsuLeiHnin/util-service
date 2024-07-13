package com.enigma.general.service.impl;

import com.enigma.general.payload.response.CommonResBody;
import com.enigma.general.service.ResponseHandlerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.enigma.general.constant.ErrorConstant.*;

@Service
public class ResponseHandlerServiceImpl implements ResponseHandlerService {
    private CommonResBody statusCodeHandler(String code){
        CommonResBody commonResBody = new CommonResBody();
        switch (code) {
            case ERR_01:
                commonResBody.setHttpStatus(HttpStatus.BAD_REQUEST);
                commonResBody.setCode(ERR_01);
                commonResBody.setMessage(ERR_MSG_01);
            case ERR_02:
                commonResBody.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                commonResBody.setCode(ERR_02);
                commonResBody.setMessage(ERR_MSG_02);
            case ERR_03:
                commonResBody.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                commonResBody.setCode(ERR_03);
                commonResBody.setMessage(ERR_MSG_03);
            case SUCCESS:
                commonResBody.setHttpStatus(HttpStatus.OK);
                commonResBody.setCode(SUCCESS);
                commonResBody.setMessage(SUCCESS_MSG);
            default:
                commonResBody.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                commonResBody.setCode(ERR_00);
                commonResBody.setMessage(ERR_MSG_00);
        }
        return commonResBody;
    }

    @Override
    public ResponseEntity<?> commonResHandler(String errorCode) {
        CommonResBody commonResBody = statusCodeHandler(errorCode);
        return new ResponseEntity(commonResBody, commonResBody.getHttpStatus());
    }
}
