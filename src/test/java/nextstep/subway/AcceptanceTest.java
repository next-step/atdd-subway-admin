package nextstep.subway;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    public static final String LOCATION_HEADER_NAME = "Location";
    protected static final String SLASH_SIGN = "/";

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }

        databaseCleanup.execute();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                          .when()
                          .get(path)
                          .then().log().all()
                          .extract();
    }

    public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured.given().log().all()
                          .body(requestBody)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post(path)
                          .then().log().all()
                          .extract();
    }

    public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
        return RestAssured.given().log().all()
                          .body(requestBody)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .put(path)
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
                          .when()
                          .delete(path)
                          .then().log().all()
                          .extract();
    }

    protected Long parseIdFromLocationHeader(ExtractableResponse<Response> response) {
        String locationValue = response.header(LOCATION_HEADER_NAME).split(SLASH_SIGN)[2];
        return Long.parseLong(locationValue);
    }
}
