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

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 7);
        ExtractableResponse<Response> stationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long upStationId = stationResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", 4);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("구간 추가하기 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addUpSectionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 7);
        ExtractableResponse<Response> stationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long upStationId = stationResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 1);
        params.put("upStationId", upStationId);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("구간 추가하기 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDownSectionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 10);
        ExtractableResponse<Response> stationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long downStationId = stationResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", 2);
        params.put("distance", 3);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("구간 추가하기 - 기존 상행 하행 사이에 등록될 경우 예외처리")
    @Test
    void addSection_DistanceGraterEqualExceptionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 7);
        ExtractableResponse<Response> stationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long upStationId = stationResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", 4);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 추가 시 동일한 상행, 하행 등록 예외처리")
    @Test
    void addSection_EqualSectionExceptionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 7);

        long lineId = lineResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 추가 시 상행과 하행역 둘 다 포함되어있지 않으면 예외처리")
    @Test
    void addSection_UpStationOrDownStation_NotContainsExceptionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 7);
        ExtractableResponse<Response> upStationResponse = 지하철역_등록("신대방");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("신림");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long upStationId = upStationResponse.body().jsonPath().getLong("id");
        long downStationId = downStationResponse.body().jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


}
