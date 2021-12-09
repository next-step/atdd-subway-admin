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

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static ExtractableResponse<Response> 지하철_노선_등록(String name, String color, Integer distance) {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        return 지하철_노선_등록(name, color, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 지하철_노선_등록(String name, String color, Long upStationId,
                                                         Long downStationId, Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return ApiUtils.post("/lines", params);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 10);

        // then
        // 지하철_노선_생성됨
        assertThat(신분당선.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {

        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-blue-600", 강남역Id, 광교역Id, 10);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(신분당선.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_등록("신분당선", "bg-red-600", 10);

        ExtractableResponse<Response> 신설동역 = 지하철역_등록("신설동");
        ExtractableResponse<Response> 까치산역 = 지하철역_등록("까치산");

        long 신설동역Id = extractId(신설동역);
        long 까치산역Id = extractId(까치산역);

        지하철_노선_등록("2호선", "bg-blue-600", 신설동역Id, 까치산역Id, 5);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = ApiUtils.get("/lines");
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");


        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);
        assertThat(lines).extracting(LineResponse::getName).contains("신분당선", "2호선");
        assertThat(lines).extracting(LineResponse::getColor).contains("bg-blue-600", "bg-red-600");
        assertThat(lines).flatExtracting(LineResponse::getStations).extracting(StationResponse::getName)
                .containsExactly("강남", "광교", "신설동", "까치산");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 10);
        Long 신분당선Id = extractId(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = ApiUtils.get("/lines/" + 신분당선Id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse line = response.body().jsonPath().getObject(".", LineResponse.class);

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
        assertThat(line.getStations().size()).isEqualTo(2);
        assertThat(line.getStations()).extracting(StationResponse::getName).containsExactly("강남", "광교");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 10);

        지하철역_등록("신설동");
        long 신분당선Id = extractId(신분당선);

        Map<String, Object> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = ApiUtils.put("/lines/" + 신분당선Id, params);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 10);
        long 신분당선Id = extractId(신분당선);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = ApiUtils.delete("/lines/" + 신분당선Id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
