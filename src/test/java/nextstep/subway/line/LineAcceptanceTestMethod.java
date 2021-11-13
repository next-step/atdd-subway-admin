package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.HttpUtils.*;
import static nextstep.subway.utils.HttpUtils.delete;
import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestMethod {

    public static void 지하철_노선_생성_확인(ExtractableResponse<Response> response) {
        assertThat(response.header("Location")).isNotBlank();
    }

    public static ExtractableResponse<Response> 신규_지하철_노선_생성_요청(String path, LineRequest request) {
        return post(path, request);
    }

    public static void 지하철_노선_생성_오류_확인(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_확인(ExtractableResponse<Response> actual, List<Long> excepted) {
        List<Long> result = actual.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(result).containsAll(excepted);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String path) {
        return get(path);
    }

    public static ExtractableResponse<Response> 지하철_노선_단건_조회(String path, Long request) {
        return get(path, request);
    }

    public static void 지하철_노선_단건_조회_확인(ExtractableResponse<Response> actual, Long excepted) {
        assertThat(actual.as(LineResponse.class).getId()).isEqualTo(excepted);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String path, LineRequest 이호선, Long id) {
        return put(path, 이호선, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(String path, Long id) {
        return delete(path, id);
    }

    public static void 응답_확인_OK(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 응답_확인_CREATED(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 응답_확인_BAD_REQUEST(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 응답_확인_NO_CONTENT(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
