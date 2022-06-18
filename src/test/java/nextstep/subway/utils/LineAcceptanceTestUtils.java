package nextstep.subway.utils;

import static nextstep.subway.utils.ResponseBodyExtractUtils.getId;
import static nextstep.subway.utils.RestAssuredUtils.delete;
import static nextstep.subway.utils.RestAssuredUtils.get;
import static nextstep.subway.utils.RestAssuredUtils.post;
import static nextstep.subway.utils.RestAssuredUtils.put;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_생성_요청;

import io.restassured.response.Response;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.dto.line.CreateLineRequest;

public class LineAcceptanceTestUtils {

    private static final String LINE_BASE_URL = "/lines";

    public static Response 지하철_노선_생성_요청(final String lineName, final String upStationName, final String downStationName) {
        Response upStationResponse = 지하철역_생성_요청(upStationName);
        Response downStationResponse = 지하철역_생성_요청(downStationName);
        CreateLineRequest createLineRequest = new CreateLineRequest(
            lineName,
            "some-color-code",
            Long.parseLong(getId(upStationResponse)),
            Long.parseLong(getId(downStationResponse)),
            10
        );
        return post(LINE_BASE_URL, createLineRequest).extract().response();
    }

    public static Response 지하철_노선_목록_조회_요청() {
        return get(LINE_BASE_URL).extract().response();
    }

    public static Response 지하철_노선_조회_요청(final String id) {
        return get(LINE_BASE_URL, id).extract().response();
    }

    public static Response 지하철_노선_수정_요청(final String id, final UpdateLineRequest updateLineRequest) {
        return put(LINE_BASE_URL, id, updateLineRequest).extract().response();
    }

    public static Response 지하철_노선_삭제_요청(final String id) {
        return delete(LINE_BASE_URL, id).extract().response();
    }
}
