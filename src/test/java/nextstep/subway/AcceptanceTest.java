package nextstep.subway;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    public final static String LINE_URL = "/lines";

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

    protected ExtractableResponse<Response> 전체_노선을_조회한다() {
        return RestAssured
                .given().log().all()
                .when().get(LINE_URL)
                .then().log().all().extract();
    }

    protected ExtractableResponse<Response> ID로_노선을_조회한다(Long id) {
        ExtractableResponse<Response> linesResponse = RestAssured
                .given().log().all()
                .when().get(LINE_URL + "/" + String.valueOf(id))
                .then().log().all().extract();
        return linesResponse;
    }

    protected ExtractableResponse 노선_생성_함수(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post(LINE_URL).
                then().
                log().all().
                extract();
        return response;
    }

    protected ExtractableResponse 노선_생성_함수(LineRequest lineRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(lineRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post(LINE_URL).
                then().
                log().all().
                extract();
        return response;
    }


}
