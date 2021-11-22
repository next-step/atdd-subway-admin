package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.ApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> 지하철_노선_등록(String name, String color, Integer distance) {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        return 지하철_노선_등록(name, color, upStationId, downStationId, distance);
    }

    private ExtractableResponse<Response> 지하철_노선_등록(String name, String color, Long upStationId,
                                                         Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return ApiUtils.post("/lines", params);
    }

    private ExtractableResponse<Response> 지하철역_등록(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return ApiUtils.post("/stations", params);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {

        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록("신분당선", "bg-blue-600", upStationId, downStationId, 10);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_등록("신분당선", "bg-red-600", 10);

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("신설동");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("까치산");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        지하철_노선_등록("2호선", "bg-blue-600", upStationId, downStationId, 5);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = ApiUtils.get("/lines");

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);
        assertThat(lines).extracting(LineResponse::getName).contains("신분당선", "2호선");
        assertThat(lines).extracting(LineResponse::getColor).contains("bg-blue-600", "bg-red-600");
        assertThat(lines).flatExtracting(LineResponse::getStations).extracting(StationResponse::getName)
                .contains("광교", "강남", "신설동", "까치산");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        ExtractableResponse<Response> createResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 10);
        Long id = createResponse.body().jsonPath().getLong("id");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = ApiUtils.get("/lines/" + id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse line = response.body().jsonPath().getObject(".", LineResponse.class);

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
        assertThat(line.getStations().size()).isEqualTo(2);
        assertThat(line.getStations()).extracting(StationResponse::getName).contains("강남", "광교");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록("신분당선", "bg-red-600", 10);

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("신설동");
        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long id = createResponse.body().jsonPath().getLong("id");

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-700");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = ApiUtils.put("/lines/" + id, params);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록("신분당선", "bg-red-600", 10);
        long id = createResponse.body().jsonPath().getLong("id");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = ApiUtils.delete("/lines/" + id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
