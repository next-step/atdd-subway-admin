package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.응답코드_확인;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    private Map<String, String> params;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        params = new HashMap<>();
    }

    /**
     * When : 지하철 노선을 생성하면 Then : 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createLine() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("판교역");
        // when
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("신분당선", "bg-red-600", 1L, 2L,
            10L);
        // then
        응답코드_확인(createResponse, HttpStatus.CREATED);
    }

    public ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }
}
