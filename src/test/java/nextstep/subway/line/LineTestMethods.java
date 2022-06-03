package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;

import static nextstep.subway.testutils.RestAssuredMethods.*;

public class LineTestMethods {
    public static final String URI_LINES = "/lines";
    public static final String SLASH = "/";

    public static ExtractableResponse<Response> 노선_생성
            (String name, String color, Long upStationId, Long downStationId, int distance) {
        return post(URI_LINES, LineRequest.of(name, color, upStationId, downStationId, distance));
    }

    public static ExtractableResponse<Response> 노선_수정(Long lineId, String newName, String newColor) {
        LineUpdateRequest lineUpdateRequest = LineUpdateRequest.of(newName, newColor);
        return put(URI_LINES + SLASH + lineId, lineUpdateRequest);
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

    public static LineResponse createLineResponse
            (String name, String color, Long upStationId, Long downStationId, int distance) {
        return 노선_생성(name, color, upStationId, downStationId, distance).as(LineResponse.class);
    }
}
