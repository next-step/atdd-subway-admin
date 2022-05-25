package nextstep.subway.line;

import static nextstep.subway.AcceptanceTest.delete;
import static nextstep.subway.AcceptanceTest.get;
import static nextstep.subway.AcceptanceTest.parseIdFromLocationHeader;
import static nextstep.subway.AcceptanceTest.post;
import static nextstep.subway.AcceptanceTest.put;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;

public class LineAcceptanceTestMethods {

    private static final String LINE_URL_PATH = "/lines";
    private static final String SLASH = "/";
    private static final String LOCATION_HEADER_KEY = "Location";

    public static ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        return post(LINE_URL_PATH, lineRequest);
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회() {
        return get(LINE_URL_PATH);
    }

    public static ExtractableResponse<Response> 지하철노선_조회(ExtractableResponse response) {
        Long targetId = parseIdFromLocationHeader(response);
        return get(LINE_URL_PATH + SLASH + targetId);
    }

    public static ExtractableResponse<Response> 지하철노선_수정(Long id, LineRequest lineRequest) {
        return put(LINE_URL_PATH + SLASH + id, lineRequest);
    }

    public static ExtractableResponse<Response> 지하철노선_제거(Long id) {
        return delete(LINE_URL_PATH + SLASH + id);
    }

    public static void 지하철노선_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION_HEADER_KEY)).isNotBlank();
    }

    public static void 지하철노선_목록_응답(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_목록_포함(ExtractableResponse response, List<ExtractableResponse> targets) {
        List<Long> targetIds = targets.stream()
            .map(target -> Long.parseLong(target.header(LOCATION_HEADER_KEY).split(SLASH)[2]))
            .collect(Collectors.toList());

        List<Long> resultIds = response.jsonPath().getList("id", Long.class);
        assertThat(resultIds).containsAll(targetIds);
    }

    public static void 지하철노선_응답(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_포함(ExtractableResponse response, ExtractableResponse target) {
        Long targetId = Long.parseLong(target.header(LOCATION_HEADER_KEY).split(SLASH)[2]);
        LineResponse resultLine = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(resultLine.getId()).isEqualTo(targetId);
    }

    public static void 지하철노선_수정됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_제거됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
