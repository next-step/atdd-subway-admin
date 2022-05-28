package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.BaseAcceptanceTest.*;

public class LineAcceptanceMethod {
    private static final String LINES_URI = "/lines";
    private static final String LINE_NAME_KEY = "name";

    public static ExtractableResponse<Response> 지하철노선_생성(LineRequest 지하철노선_요청) {
        Map<String, Object> params = objectMapper.convertValue(지하철노선_요청, Map.class);
        return post(LINES_URI, params);
    }

    public static List<String> 지하철노선_목록_조회() {
        return get(LINES_URI).jsonPath().getList(LINE_NAME_KEY, String.class);
    }

    public static void 생성한_지하철노선_찾기(String... 지하철노선_이름) {
        List<String> 지하철노선_목록 = 지하철노선_목록_조회();
        assertThat(지하철노선_목록).contains(지하철노선_이름);
    }
}
