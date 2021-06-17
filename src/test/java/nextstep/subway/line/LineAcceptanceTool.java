package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

public class LineAcceptanceTool {

    public static ExtractableResponse 노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    public static LineRequest 노선_요청_정보(String name, String color) {
        return new LineRequest(name, color);
    }

    public static ExtractableResponse 노선_목록_조회() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse 노선_조회(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + id)
            .then().log().all().extract();
    }

    public static ExtractableResponse 노선_수정(Long id, LineRequest fixedRequest) {
        return RestAssured.given().log().all()
            .body(fixedRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();
    }

    public static ExtractableResponse 노선_삭제(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all().extract();
    }

    public static Long 생성_노선_아이디(ExtractableResponse createResponse) {
        return createResponse.jsonPath().getObject(".", LineResponse.class).getId();
    }
}
