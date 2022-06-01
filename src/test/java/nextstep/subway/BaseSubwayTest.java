package nextstep.subway;

import static nextstep.subway.ui.UrlConstant.CREATE_STATIONS;
import static nextstep.subway.ui.UrlConstant.SHOW_STATIONS;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSubwayTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    protected ExtractableResponse<Response> 지하철_생성(final String name) {
        return RestAssured.given().log().all()
                .body(Collections.singletonMap("name", name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(CREATE_STATIONS)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 지하철_목록_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(SHOW_STATIONS)
                .then().log().all()
                .extract();
    }
}
