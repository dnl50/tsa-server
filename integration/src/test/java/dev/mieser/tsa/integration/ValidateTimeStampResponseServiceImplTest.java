package dev.mieser.tsa.integration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.mieser.tsa.domain.TimeStampValidationResult;
import dev.mieser.tsa.signing.api.TimeStampValidator;

@ExtendWith(MockitoExtension.class)
class ValidateTimeStampResponseServiceImplTest {

    private final TimeStampValidator timeStampValidatorMock;

    private final ValidateTimeStampResponseServiceImpl testSubject;

    ValidateTimeStampResponseServiceImplTest(@Mock TimeStampValidator timeStampValidatorMock) {
        this.timeStampValidatorMock = timeStampValidatorMock;
        this.testSubject = new ValidateTimeStampResponseServiceImpl(timeStampValidatorMock);
    }

    @Test
    void validateTimeStampResponseDecodesBase64() {
        // given
        byte[] tspResponse = "TSP response".getBytes(UTF_8);
        String base64EncodedResponse = encodeBase64String(tspResponse);
        TimeStampValidationResult validationResult = TimeStampValidationResult.builder().build();

        given(timeStampValidatorMock.validateResponse(notNull())).willReturn(validationResult);

        // when
        TimeStampValidationResult actualValidationResult = testSubject.validateTimeStampResponse(base64EncodedResponse);

        // then
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        then(timeStampValidatorMock).should().validateResponse(inputStreamCaptor.capture());

        assertSoftly(softly -> {
            softly.assertThat(inputStreamCaptor.getValue()).hasBinaryContent(tspResponse);
            softly.assertThat(actualValidationResult).isEqualTo(validationResult);
        });
    }

}
