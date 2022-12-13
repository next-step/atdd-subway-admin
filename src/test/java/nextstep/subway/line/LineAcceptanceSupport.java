package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import nextstep.subway.dto.request.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceSupport {

    public static ExtractableResponse 지하철_노선_조회(String id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse 지하철_노선_수정(ExtractableResponse createLine, LineRequest updateLineRequest) {
        return RestAssured.given().log().all()
                .pathParam("id", createLine.jsonPath().get("id"))
                .body(updateLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }


    public static ExtractableResponse 지하철_노선_삭제(ExtractableResponse createLine) {
        return RestAssured.given().log().all()
                .pathParam("id", createLine.jsonPath().get("id"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
