package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineUpdateRequest;

import static nextstep.subway.testutils.RestAssuredMethods.*;

public class LineTestMethods {
    public static final String URI_LINES = "/lines";
    public static final String SLASH = "/";

    public static ExtractableResponse<Response> 노선_생성(LineRequest lineRequest) {
        return post(URI_LINES, lineRequest);
    }

    public static ExtractableResponse<Response> 노선_수정(Long lineId, LineUpdateRequest newLine) {
        return put(URI_LINES + SLASH + lineId, newLine);
    }

    public static ExtractableResponse<Response> 노선_전체_조회() {
        return get(URI_LINES);
    }

    public static ExtractableResponse<Response> 노선_단건_조회(Long lineId) {
        return get(URI_LINES + SLASH + lineId);
    }

    public static ExtractableResponse<Response> 노선_삭제(Long lineId) {
        return delete(URI_LINES + SLASH + lineId);
    }
}
