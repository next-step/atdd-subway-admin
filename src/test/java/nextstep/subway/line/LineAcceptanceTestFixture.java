package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.domain.line.Line;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

class LineAcceptanceTestFixture {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * 지하철 노선 생성
     *
     * @param lineName 생성할 지하철 노선 이름
     * @return 생성된 지하철 노선 id
     */
    protected static ExtractableResponse<Response> 지하철_노선_생성(String lineName, String lineColor) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", lineColor);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 목록 조회
     *
     * @return 지하철 노선 목록 조회 Response
     */
    protected static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 정보 조회
     *
     * @return 지하철 노선 정보 조회 Response
     */
    protected static ExtractableResponse<Response> 지하철_노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    protected static Line 노선정보(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("", Line.class);
    }

    protected static List<Line> 노선목록(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("", Line.class);
    }
}
