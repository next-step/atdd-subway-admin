package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.line.dto.SectionRequest;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Long> 등록되지_않은_구간() {
        HashMap<String, Long> terminus = new HashMap<>();
        terminus.put("상행", 1L);
        terminus.put("하행", 2L);
        return terminus;
    }
}
