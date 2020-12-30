package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private List<LineResponse> lineResponses = new ArrayList<>();

    @BeforeEach
    void beforeSetUp() {
        StationResponse 강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);

        Long upStationId = 강남역.getId();
        Long downStationId = 역삼역.getId();

        lineResponses.add(지하철_노선_생성("신분당선", "bg-red-600", upStationId, downStationId, 10)
                        .as(LineResponse.class));
        lineResponses.add(지하철_노선_생성("2호선", "bg-green-600", upStationId, downStationId, 20)
                        .as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response =
                지하철_노선_생성("1호선", "bg-blue-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // when
        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = 응답_데이터에서_지하철_노선_id들_추출(lineResponses);
        List<Long> resultLineIds = 응답_데이터에서_지하철_노선_id들_추출(Arrays.asList(response.as(LineResponse[].class)));
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // when
        LineResponse createLineResponse = lineResponses.get(0);
        ExtractableResponse<Response> response = 지하철_노선_조회(createLineResponse.getId().toString());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // when
        LineResponse createLineResponse = lineResponses.get(0);
        ExtractableResponse<Response> response = 지하철_노선_수정(createLineResponse.getId(), "구분당선", "bg-blue-600");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // when
        LineResponse createLineResponse = lineResponses.get(0);
        ExtractableResponse<Response> response = 지하철_노선_제거(createLineResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String name, String color,
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

    private ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
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

    private ExtractableResponse<Response> 지하철_노선_제거(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private List<Long> 응답_데이터에서_지하철_노선_id들_추출(List<LineResponse> lineResponses) {
        return lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
