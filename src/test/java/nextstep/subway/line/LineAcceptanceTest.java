package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.util.DataBaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.line.accecptance.LineAcceptanceRequest.지하철노선_목록_조회_요청;
import static nextstep.subway.line.accecptance.LineAcceptanceRequest.지하철노선_생성_요청;
import static nextstep.subway.line.accecptance.LineAcceptanceRequest.지하철노선_조회_요청;
import static nextstep.subway.line.accecptance.LineAcceptanceRequest.지하철노선_존재;
import static nextstep.subway.line.accecptance.LineAcceptanceResponse.지하철노선_목록_조회_성공;
import static nextstep.subway.line.accecptance.LineAcceptanceResponse.지하철노선_생성_성공;
import static nextstep.subway.line.accecptance.LineAcceptanceResponse.지하철노선_조회_성공;

@DisplayName("지하철 노선 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DataBaseCleanUp dataBaseCleanUp;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            dataBaseCleanUp.afterPropertiesSet();
        }
        dataBaseCleanUp.execute();
    }

    /**
     * WHEN 지하철 노선을 생성하면
     * THEN 지하철 노선이 생성되고
     * THEN 지하철 노선을 조회 시 생성한 노선을 볼 수 있다
     */
    @DisplayName("지하철노선 생성한다")
    @Test
    void 지하철노선_생성_조회_성공() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청();

        //then
        지하철노선_생성_성공(response);
    }

    /**
     * Given 2개의 지하철 노선을 존재하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회한다")
    @Test
    void 지하철노선_목록_조회() {
        지하철노선_존재("2호선");
        지하철노선_존재("1호선");

        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        지하철노선_목록_조회_성공(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회한다")
    @Test
    void 지하철노선_조회() {
        지하철노선_존재("2호선");

        ExtractableResponse<Response> response = 지하철노선_조회_요청();

        지하철노선_조회_성공(response);
    }
}
