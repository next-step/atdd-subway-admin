package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

    protected ExtractableResponse<Response> GET_요청_보내기(final String uri) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> POST_요청_보내기(final Map<String, String> params, final String uri) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(uri)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> PUT_요청_보내기(final String uri, final Map<String, String> params) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> DELETE_요청_보내기(final String uri) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }
}
