package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.line.dto.SectionRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineScenarioMethod.지하철_노선_조회_요청;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

class SectionScenarioMethod {

    public static SectionRequest 지하철_구간_정보(Map<String, Long> stations, String upStationKey, String downStationKey, int distance) {
        return new SectionRequest(stations.get(upStationKey), stations.get(downStationKey), distance);
    }

    public static ExtractableResponse<Response> 지하철_구간_생성_요청(String uri, SectionRequest request) {
        return RestAssuredApi.post(uri + "/sections", request);
    }

    public static void 지하철_구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_구간_생성_실패됨(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void 지하철_노선에_등록한_구간_포함됨(String uri, List<String> stationNames) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);
        response.jsonPath().getList("stations.name");
        assertThat(response.jsonPath().getList("stations.name"))
                .isEqualTo(stationNames);
    }
    public static void 지하철_구간_개수_일치됨(ExtractableResponse<Response> response, int size) {
        List<String> result = response.jsonPath().getList("upStation.name");
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .isEqualTo(Arrays.asList("강남", "양재", "양재시민의숲"));
    }

    public static void 지하철_구간_상행역_일치됨(ExtractableResponse<Response> response, List<String> stationNames) {
        List<String> result = response.jsonPath().getList("upStation.name");
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .isEqualTo(Arrays.asList("강남", "양재", "양재시민의숲"));
    }
}