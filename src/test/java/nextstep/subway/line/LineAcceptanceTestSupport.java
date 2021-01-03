package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTestSupport;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTestSupport extends AcceptanceTest {

    private static Long upStationId = null;
    private static Long downStationId = null;

    public static Long getUpStationId() {
        return upStationId;
    }

    public static Long getDownStationId() {
        return downStationId;
    }

    private static void setUpStationId(String upStationName) {
        if (LineAcceptanceTestSupport.upStationId == null) {
            LineAcceptanceTestSupport.upStationId
                    = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(upStationName))
                    .as(StationResponse.class).getId();
        }
    }

    private static void setDownStationId(String downStationName) {
        if(LineAcceptanceTestSupport.downStationId == null) {
            LineAcceptanceTestSupport.downStationId
                    = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(downStationName))
                    .as(StationResponse.class).getId();
        }
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color
    , String upStationName, String downStationName, int distance) {
        LineAcceptanceTestSupport.setUpStationId(upStationName);
        LineAcceptanceTestSupport.setDownStationId(downStationName);

        return LineAcceptanceTestSupport.지하철_노선_생성_요청(
                new LineRequest(name, color
                        , LineAcceptanceTestSupport.upStationId, LineAcceptanceTestSupport.downStationId, distance));
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
    public static ExtractableResponse<Response> 지하철_노선_수정_요청(LineRequest lineRequest, String requestPath) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
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
