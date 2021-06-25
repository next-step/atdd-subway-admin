package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTool;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTool extends AcceptanceTest {

    private static Long upStationId = null;
    private static Long downStationId = null;

    public static Long getUpStationId() {
        return upStationId;
    }

    public static Long getDownStationId() {
        return downStationId;
    }

    private static void setUpStationId(String upStationName) {
        LineAcceptanceTool.upStationId
            = StationAcceptanceTool.지하철_역_생성_요청(new StationRequest(upStationName))
            .as(StationResponse.class).getId();
    }

    private static void setDownStationId(String downStationName) {
        LineAcceptanceTool.downStationId
            = StationAcceptanceTool.지하철_역_생성_요청(new StationRequest(downStationName))
            .as(StationResponse.class).getId();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color
        , String upStationName, String downStationName, int distance) {
        LineAcceptanceTool.setUpStationId(upStationName);
        LineAcceptanceTool.setDownStationId(downStationName);

        return LineAcceptanceTool.지하철_노선_생성_요청(
            new LineRequest(name, color
                , LineAcceptanceTool.upStationId, LineAcceptanceTool.downStationId,
                distance));
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(String requestPath) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(requestPath)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String name, String color,
        String requestPath) {

        LineRequest modifiedLineRequest = new LineRequest(name, color, upStationId, downStationId, 10);

        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(modifiedLineRequest)
            .when()
            .put(requestPath)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(String requestPath) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(requestPath)
            .then().log().all()
            .extract();
    }
}
