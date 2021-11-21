package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineApiRequests {
    public static  ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static  ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static  ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static  void 지하철_노선_수정_요청(Long lineId, LineRequest updateRequest) {
        RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static  void 지하철_노선_제거_요청(Long lineId) {
        RestAssured
                .given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then().log().all();
    }
}
