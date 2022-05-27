package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능 구현")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철_노선을_생성한다() {
        // when
        지하철_노선_생성("2호선", "green");

        // then
        List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();
        목록에_생성한_노선이_포함된다(지하철_노선_목록, "2호선");
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String line, String color) {
        Long 강남역_id = id_추출(지하철역_생성("강남역"));
        Long 잠실역_id = id_추출(지하철역_생성("잠실역"));
        LineRequest request = new LineRequest(line, color, 강남역_id, 잠실역_id, 10);

        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private Long id_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private List<String> 지하철_노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath()
                .getList( "name", String.class);
    }

    private void 목록에_생성한_노선이_포함된다(List<String> lineNames, String line) {
        assertThat(lineNames).containsExactly(line);
    }
}
