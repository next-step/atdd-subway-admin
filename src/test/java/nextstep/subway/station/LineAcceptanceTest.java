package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.LineAcceptanceTest.LineAcceptanceTemplate.*;
import static nextstep.subway.station.StationAcceptanceTest.StationAcceptanceTemplate.지하철역_생성;
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
        지하철_노선_생성("2호선", "green", "강남역", "잠실역");

        // then
        List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();
        목록에_생성한_노선이_포함된다(지하철_노선_목록, "2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        지하철_노선_생성("2호선", "green", "강남역", "잠실역");
        지하철_노선_생성("4호선", "blue", "사당역", "안산역");
        
        // when
        List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();

        // then
        노선_목록_사이즈를_확인한다(지하철_노선_목록, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철_노선을_조회한다() {
        // given
        ExtractableResponse<Response> _2호선 = 지하철_노선_생성("2호선", "green", "강남역", "잠실역");

        // when
        LineResponse 조회된_지하철_노선 = 지하철_노선_단건_조회(_2호선);

        // then
        노선_정상_응답을_확인한다(조회된_지하철_노선);
    }

    private LineResponse 지하철_노선_단건_조회(ExtractableResponse<Response> response) {
        return RestAssured
                .given().log().all()
                .when().post("/lines" + response.body().jsonPath().getLong("id"))
                .then().log().all()
                .extract().body().as(LineResponse.class);
    }

    static class LineAcceptanceTemplate {
        static ExtractableResponse<Response> 지하철_노선_생성(String line, String color, String upStationName, String downStationName) {
            Long 상행역_id = id_추출(지하철역_생성(upStationName));
            Long 하행역_id = id_추출(지하철역_생성(downStationName));
            LineRequest request = new LineRequest(line, color, 상행역_id, 하행역_id, 10);

            return RestAssured
                    .given().log().all()
                    .body(request)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/lines")
                    .then().log().all()
                    .extract();
        }

        static Long id_추출(ExtractableResponse<Response> response) {
            return response.jsonPath().getLong("id");
        }

        static List<String> 지하철_노선_목록_조회() {
            return RestAssured
                    .given().log().all()
                    .when().get("/lines")
                    .then().log().all()
                    .extract().jsonPath()
                    .getList( "name", String.class);
        }

        static void 목록에_생성한_노선이_포함된다(List<String> lineNames, String line) {
            assertThat(lineNames).containsExactly(line);
        }

        static void 노선_목록_사이즈를_확인한다(List<String> 지하철_노선_목록, int size) {
            assertThat(지하철_노선_목록).hasSize(size);
        }

        static void 노선_정상_응답을_확인한다(LineResponse 조회된_지하철_노선) {
            assertThat(조회된_지하철_노선).isNotNull();
        }
    }
}
