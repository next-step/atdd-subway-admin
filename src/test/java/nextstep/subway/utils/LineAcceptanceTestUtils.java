package nextstep.subway.utils;

import static nextstep.subway.utils.CommonTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;

public class LineAcceptanceTestUtils {
    public static final String 신분당선 = "신분당선";
    public static final String 경춘선 = "경춘선";
    public static final String 수인분당선 = "수인분당선";
    public static final String 신사역 = "신사역";
    public static final String 논현역 = "논현역";
    public static final String 신논현역 = "신논현역";
    public static final String 판교역 = "판교역";
    public static final String 강남역 = "강남역";
    public static final String 대성리역 = "대성리역";
    public static final String 가평역 = "가평역";

    public static ExtractableResponse<Response> 지하철노선을_생성한다(LineRequest lineRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME, lineRequest.getName());
        params.put(COLOR, lineRequest.getColor());
        params.put("upStationId", lineRequest.getUpStationId());
        params.put("downStationId", lineRequest.getDownStationId());
        params.put("distance", lineRequest.getDistance());

        return 생성(LINE_BASE_PATH, params);
    }

    public static List<String> 지하철노선_목록을_조회한다() {
        return 목록_조회(LINE_BASE_PATH);
    }

    public static ExtractableResponse<Response> 지하철노선을_조회한다(ExtractableResponse<Response> response) {
        return 상세_조회(LINE_BASE_PATH, 응답_ID_추출(response));
    }

    public static ExtractableResponse<Response> 지하철노선을_수정한다(ExtractableResponse<Response> response, String updateName, String updateColor) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME, updateName);
        params.put(COLOR, updateColor);

        return 수정(LINE_BASE_PATH, params, 응답_ID_추출(response));
    }

    public static ExtractableResponse<Response> 지하철노선을_삭제한다(ExtractableResponse<Response> response) {
        return 삭제(LINE_BASE_PATH + PATH_VARIABLE_ID, 응답_ID_추출(response));
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재(List<String> actualNames, String... lineNames) {
        목록_검증_존재함(actualNames, lineNames);
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재하지_않음(List<String> actualNames, String... lineNames) {
        목록_검증_존재하지_않음(actualNames, lineNames);
    }

    public static void 지하철노선_검증_입력된_지하철노선이_존재(ExtractableResponse<Response> response, String lineName){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString(NAME)).isEqualTo(lineName);
    }
}
