package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceMethods;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;

public class LineAcceptanceMethods extends AcceptanceMethods {

    private static final String LINE_PATH = "/lines";

    private LineAcceptanceMethods() {
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
        return post(LINE_PATH, lineRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return get(LINE_PATH);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return get(LINE_PATH + SLASH + id);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(Long id, LineUpdateRequest lineRequest) {
        return put(LINE_PATH + SLASH + id, lineRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        return delete(LINE_PATH + SLASH + id);
    }
}
