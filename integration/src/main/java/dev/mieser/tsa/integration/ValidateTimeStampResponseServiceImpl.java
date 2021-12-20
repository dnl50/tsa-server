package dev.mieser.tsa.integration;

import dev.mieser.tsa.domain.TimestampValidationResult;
import dev.mieser.tsa.integration.api.ValidateTimeStampResponseService;
import dev.mieser.tsa.signing.api.TimeStampValidator;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

@RequiredArgsConstructor
public class ValidateTimeStampResponseServiceImpl implements ValidateTimeStampResponseService {

    private final TimeStampValidator timeStampValidator;

    @Override
    public TimestampValidationResult validateTimeStampResponse(String base64EncodedResponse) {
        InputStream tspResponseStream = new ByteArrayInputStream(decodeBase64(base64EncodedResponse));
        return timeStampValidator.validateResponse(tspResponseStream);
    }

}