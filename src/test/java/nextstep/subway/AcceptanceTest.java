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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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


    protected ExtractableResponse<Response> get(final String url) {
        // when
        return RestAssured.given().log().all()
                .when().get(url)
                .then().log().all().extract();
    }

    protected ExtractableResponse<Response> post(final String url, final Object params) {
        // when
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(url)
                .then().log().all().extract();
    }

    protected ExtractableResponse<Response> put(final String url, final Object params) {
        // when
        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put(url)
                          .then().log().all().extract();
    }

    protected ExtractableResponse<Response> delete(final String url) {
        // when
        return RestAssured.given().log().all()
                          .when().delete(url)
                          .then().log().all().extract();
    }
}
