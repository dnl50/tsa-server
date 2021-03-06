package dev.mieser.tsa.app.test;

import static dev.mieser.tsa.domain.HashAlgorithm.SHA256;
import static dev.mieser.tsa.domain.HashAlgorithm.SHA512;
import static dev.mieser.tsa.domain.ResponseStatus.GRANTED;
import static dev.mieser.tsa.domain.ResponseStatus.REJECTION;
import static io.restassured.RestAssured.given;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.tsp.TimeStampResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TimeStampAuthorityControllerIntegrationTest {

    private final int serverPort;

    TimeStampAuthorityControllerIntegrationTest(@Value("${tsa.server.port}") int serverPort) {
        this.serverPort = serverPort;
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    void issueTimeStampTokenWithAcceptedHashAlgorithm() throws Exception {
        // given
        var sha512Identifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(SHA512.getObjectIdentifier()));
        var messageImprint = new MessageImprint(sha512Identifier, repeat("a", 64).getBytes(UTF_8));
        var tspRequest = new TimeStampReq(messageImprint, null, null, ASN1Boolean.TRUE, null);

        // when / then
        byte[] asnEncodedRspResponse = given()
            .contentType("application/timestamp-query")
            .accept("application/timestamp-reply")
            .body(tspRequest.getEncoded())
            .when()
            .post("/")
            .then()
            .statusCode(SC_OK)
            .extract()
            .body().asByteArray();

        var tspResponse = new TimeStampResponse(asnEncodedRspResponse);
        assertThat(tspResponse.getStatus()).isEqualTo(GRANTED.getValue());
    }

    @Test
    void issueTimeStampTokenWithUnknownHashAlgorithm() throws Exception {
        // given
        var md5Identifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.2.5"));
        var messageImprint = new MessageImprint(md5Identifier, repeat("a", 16).getBytes(UTF_8));
        var tspRequest = new TimeStampReq(messageImprint, null, null, ASN1Boolean.TRUE, null);

        // when / then
        given()
            .contentType("application/timestamp-query")
            .accept("application/timestamp-reply")
            .body(tspRequest.getEncoded())
            .when()
            .post("/")
            .then()
            .statusCode(SC_BAD_REQUEST);
    }

    @Test
    void issueTimeStampTokenWithIllegalHashAlgorithm() throws Exception {
        // given
        var sha256Identifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(SHA256.getObjectIdentifier()));
        var messageImprint = new MessageImprint(sha256Identifier, repeat("a", 32).getBytes(UTF_8));
        var tspRequest = new TimeStampReq(messageImprint, null, null, ASN1Boolean.TRUE, null);

        // when / then
        byte[] asnEncodedRspResponse = given()
            .contentType("application/timestamp-query")
            .accept("application/timestamp-reply")
            .body(tspRequest.getEncoded())
            .when()
            .post("/")
            .then()
            .statusCode(SC_OK)
            .extract()
            .body().asByteArray();

        var tspResponse = new TimeStampResponse(asnEncodedRspResponse);
        assertThat(tspResponse.getStatus()).isEqualTo(REJECTION.getValue());
    }

    @Test
    void issueTimeStampTokenWithWronglyFormattedRequest() {
        // given
        byte[] wronglyFormattedRequest = "TSP request".getBytes(UTF_8);

        // when / then
        given()
            .contentType("application/timestamp-query")
            .accept("application/timestamp-reply")
            .body(wronglyFormattedRequest)
            .when()
            .post("/")
            .then()
            .statusCode(SC_BAD_REQUEST);
    }

    @Test
    void doesNotAcceptRequestsWithWrongContentType() {
        // given / when / then
        given()
            .contentType("text/plain")
            .accept("application/timestamp-reply")
            .when()
            .post("/")
            .then()
            .statusCode(SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void doesNotAcceptRequestsWithWrongRequestMethod() {
        // given / when / then
        given()
            .contentType("application/timestamp-query")
            .accept("application/timestamp-reply")
            .when()
            .put("/")
            .then()
            .statusCode(SC_METHOD_NOT_ALLOWED);
    }

}
