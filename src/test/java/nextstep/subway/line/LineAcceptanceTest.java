package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");

        // when
        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");
        지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");
        ExtractableResponse<Response> createResponse1 =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse2 =
                지하철_노선_생성("2호선", "bg-green-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> createResponses = Arrays.asList(createResponse1.as(LineResponse.class), createResponse2.as(LineResponse.class));
        List<Long> expectedLineIds = 응답_데이터에서_지하철_노선_id들_추출(createResponses);
        List<Long> resultLineIds = 응답_데이터에서_지하철_노선_id들_추출(Arrays.asList(response.as(LineResponse[].class)));
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");
        ExtractableResponse<Response> createResponse =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        String id = 응답_데이터에서_지하철_노선_id_추출(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");
        ExtractableResponse<Response> createResponse =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        String id = 응답_데이터에서_지하철_노선_id_추출(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_수정(id, "구분당선", "bg-blue-600");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationAcceptanceTest.지하철역_생성("강남역");
        StationAcceptanceTest.지하철역_생성("역삼역");
        ExtractableResponse<Response> createResponse =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        String id = 응답_데이터에서_지하철_노선_id_추출(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_제거(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color,
                                                    Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return 지하철_노선_조회("");
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(String id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거(String id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private String 응답_데이터에서_지하철_노선_id_추출(ExtractableResponse<Response> response) {
        return response.header("Location").split("/")[2];
    }

    private List<Long> 응답_데이터에서_지하철_노선_id들_추출(List<LineResponse> lineResponses) {
        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
