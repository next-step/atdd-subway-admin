package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.MediaType;

public class LineTestHelper {
    private LineTestHelper() {
    }

    static Long 지하철_노선_생성_하고_ID_응답(LineRequest lineRequest) {
        return 지하철_노선_생성(lineRequest)
                .jsonPath().getLong("id");
    }

    static ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_ID로_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_전체목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정(Long lineId, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_ID로_삭제(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }
}
