package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAssuredMethod {

    private static final String LINES_RESOURCE = "/lines";
    private static final String LINES_RESOURCE_ID = "/lines/{id}";

    public static ExtractableResponse<Response> 노선_생성_요청(LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(LINES_RESOURCE_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_수정_요청(Long id, LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINES_RESOURCE_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .when().get(LINES_RESOURCE_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_전체_조회_요청() {
        return RestAssured.given().log().all()
                .when().get(LINES_RESOURCE)
                .then().log().all()
                .extract();
    }
}
