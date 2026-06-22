package com.maersk.wms.picking.api.dto;

import com.maersk.wms.picking.domain.DecodeResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecodeResponse {
    private String decodedType;
    private boolean validated;
    private String decodedValue;
    private String errorCode;
    private String message;

    public static DecodeResponse from(DecodeResult result) {
        return DecodeResponse.builder()
                .decodedType(result.getDecodedType().name())
                .validated(result.isValidated())
                .decodedValue(result.getActualValue())
                .errorCode(result.getErrorCode())
                .message(result.getMessage())
                .build();
    }
}
