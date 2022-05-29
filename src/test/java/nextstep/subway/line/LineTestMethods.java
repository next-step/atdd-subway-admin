package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.MediaType;

public class LineTestMethods {

    public static final String URI_LINES = "/lines";
    public static final String URI_LINES_ID = "/lines/{id}";

    public static ExtractableResponse<Response> 노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URI_LINES)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_수정(Long lineId, LineUpdateRequest newLine) {
        return RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI_LINES_ID, lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_전체_조회() {
        return RestAssured.given().log().all()
                .when().get(URI_LINES)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_단건_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get(URI_LINES_ID, lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete(URI_LINES_ID, lineId)
                .then().log().all()
                .extract();
    }
}
