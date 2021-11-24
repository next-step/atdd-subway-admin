package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.ApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간 추가하기 - 기존 상행 하행 사이에 등록될 경우")
    @Test
    void addSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);
        long addDownStationId = extractId(addStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("구간 추가하기 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addUpSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);

        long lineId = extractId(lineResponse);
        long addStationId = extractId(addStationResponse);

        int distance = 4;

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", upStationId);
        params.put("upStationId", addStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("구간 추가하기 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDownSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 10);

        long lineId = extractId(lineResponse);
        long addStationId = extractId(addStationResponse);

        int distance = 3;
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", addStationId);
        params.put("upStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("구간 추가하기 - 추가될 구간의 거리가 동일하거나 큰 경우 예외처리")
    @Test
    void addSection_DistanceGraterEqualExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        ExtractableResponse<Response> addUpStationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long addUpStationId = extractId(addUpStationResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", addUpStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 추가 시 동일한 상행, 하행 등록 예외처리")
    @Test
    void addSection_EqualSectionExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 추가 시 상행과 하행역 둘 다 포함되어있지 않으면 예외처리")
    @Test
    void addSection_UpStationOrDownStation_NotContainsExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        ExtractableResponse<Response> addUpStationResponse = 지하철역_등록("신대방");
        ExtractableResponse<Response> addDownStationResponse = 지하철역_등록("신림");

        long addUpStationId = extractId(addUpStationResponse);
        long addDownStationId = extractId(addDownStationResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", addUpStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


}
