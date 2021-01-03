package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

import java.util.List;

public class LineAcceptanceTestRequest {
    public static LineResponse 지하철_노선_생성_요청(String name, String color, long upStationId, long downStationId, long distance) {
        LineRequest request = createLineRequest(name, color, upStationId, downStationId, distance);
        return 지하철_노선_생성_요청(request)
                .body().as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청후_응답(String name, String color, long upStationId, long downStationId, long distance) {
        return 지하철_노선_생성_요청(createLineRequest(name, color, upStationId, downStationId, distance));
    }

    public static ExtractableResponse<Response> 지하철_노선_재생성후_응답(String name, String color, long upStationId, long downStationId, long distance) {
        return 지하철_노선_생성_요청(createLineRequest(name, color, upStationId, downStationId, distance));
    }

    public static List<LineResponse> 지하철_노선_목록_조회_요청() {
        return 지하철_노선_목록_조회()
                .jsonPath().getList(".", LineResponse.class);
    }

    public static LineResponse 지하철_노선_조회(long id) {
        return 지하철_노선_조회_요청(id)
                .body().as(LineResponse.class);
    }

    private static ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
        return RestAssured
                .given().log().all()
                .when().get("lines/" + id)
                .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청후_응답(ExtractableResponse<Response> createdResponse, String name, String color) {
        String createdUrl = convertLocationUrl(createdResponse);
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(createLineRequest(name, color))
                .when().put(createdUrl)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createdResponse) {
        String createdUrl = convertLocationUrl(createdResponse);
        return RestAssured
                .given().log().all()
                .when().delete(createdUrl)
                .then().log().all().extract();
    }

    private static String convertLocationUrl(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private static LineRequest createLineRequest(String name, String color) {
        return new LineRequest(name, color);
    }

    private static LineRequest createLineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
}
