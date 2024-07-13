package com.enigma.general.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonResBody {
    @JsonIgnore
    private HttpStatus httpStatus;
    private String code;
    private String message;
}
