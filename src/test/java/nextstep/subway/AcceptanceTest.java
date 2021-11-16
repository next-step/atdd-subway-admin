package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class AcceptanceTest {
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

    private RequestSpecification given() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    protected ExtractableResponse<Response> 저장한다(Object body, String url) {
        return given().body(body)
            .when()
            .post(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 조회한다(String url) {
        return given()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 수정한다(Object body, String url) {
        return given()
            .body(body)
            .when()
            .put(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 삭제한다(String url) {
        return given()
            .when()
            .delete(url)
            .then().log().all()
            .extract();
    }
}
