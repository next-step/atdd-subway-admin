package nextstep.subway.line;

import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredBuilder;
import org.springframework.http.MediaType;

public class LineTestFixture {
    public static final String BASE_LINE_URL = "/lines";

    private LineTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static LineRequest 신분당선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("신분당선", "bg-red-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static LineRequest 구분당선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("구분당선", "bg-blue-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static LineRequest 이호선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("2호선", "bg-green-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return new RestAssuredBuilder(Method.POST, BASE_LINE_URL)
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBody(lineRequest)
                .build();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return 지하철_노선_목록_조회_요청(BASE_LINE_URL);
    }

    public static ExtractableResponse<Response> 특정_지하철_노선_조회_요청(LineResponse lineResponse) {
        return 지하철_노선_목록_조회_요청(BASE_LINE_URL + "/" + lineResponse.getId());
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String requestUrl) {
        return new RestAssuredBuilder(Method.GET, requestUrl)
                .build();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse lineResponse, LineRequest lineRequest) {
        return new RestAssuredBuilder(Method.PUT, BASE_LINE_URL + "/" + lineResponse.getId())
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBody(lineRequest)
                .build();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse lineResponse) {
        return new RestAssuredBuilder(Method.DELETE, BASE_LINE_URL + "/" + lineResponse.getId())
                .build();
    }

    public static LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest)
                .as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 노선에_구간_추가_요청(LineResponse lineResponse, SectionAddRequest sectionAddRequest) {
        return new RestAssuredBuilder(Method.POST, BASE_LINE_URL + "/" + lineResponse.getId() + "/sections")
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBody(sectionAddRequest)
                .build();
    }

    public static ExtractableResponse<Response> 노선에_구간_제거_요청(LineResponse lineResponse, StationResponse stationResponse) {
        return new RestAssuredBuilder(Method.DELETE, BASE_LINE_URL + "/" + lineResponse.getId() + "/sections?stationId=" + stationResponse.getId())
                .build();
    }
}
