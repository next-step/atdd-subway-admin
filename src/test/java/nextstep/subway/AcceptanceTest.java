package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

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

}
