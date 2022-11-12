package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceMethodsTestFixture;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineUpdateRequest;

import static nextstep.subway.station.StationAcceptanceMethods.지하철역_생성;

public class LineAcceptanceMethods extends AcceptanceMethodsTestFixture {

    public static final String LINE_PATH = "/lines";

    private LineAcceptanceMethods() {
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String name,
                                                          String color,
                                                          String upStationName,
                                                          String downStationName,
                                                          int distance) {
        long upStationId = 지하철역_생성(upStationName).body().jsonPath().getLong("id");
        long downStationId = 지하철역_생성(downStationName).body().jsonPath().getLong("id");

        return post(LINE_PATH, LineRequest.of(name, color, upStationId, downStationId, distance));
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String name,
                                                          String color,
                                                          Long upStationId,
                                                          Long downStationId,
                                                          int distance) {
        return post(LINE_PATH, LineRequest.of(name, color, upStationId, downStationId, distance));
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
