package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.BaseAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceMethod {
    private static final String STATIONS_URI = "/stations";
    private static final String STATION_NAME_KEY = "name";

    public static ExtractableResponse<Response> 지하철역_생성(String 지하철역_이름) {
        Map<String, String> params = new HashMap<>();
        params.put(STATION_NAME_KEY, 지하철역_이름);
        return post(STATIONS_URI, params);
    }

    public static void 지하철역_생성됨(ExtractableResponse<Response> 지하철역_생성_응답) {
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static List<String> 지하철역_목록_조회() {
        return get(STATIONS_URI).jsonPath().getList(STATION_NAME_KEY, String.class);
    }

    public static void 생성한_지하철역_찾기(String... 지하철역_이름) {
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        assertThat(지하철역_목록).contains(지하철역_이름);
    }

    public static void 지하철역_생성_안됨(ExtractableResponse<Response> 지하철역_생성_응답) {
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_삭제(ExtractableResponse<Response> 지하철역_생성_응답) {
        delete(지하철역_생성_응답.header(HttpHeaders.LOCATION));
    }

    public static void 지하철역_목록에서_찾을수_없음(String... 지하철역_이름) {
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        assertThat(지하철역_목록).doesNotContain(지하철역_이름);
    }
}
