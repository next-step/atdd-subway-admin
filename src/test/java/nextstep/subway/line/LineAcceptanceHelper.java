package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceHelper {
    static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    static void 지하철_노선_목록_조회_됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    static ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("lines/1")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> updateParams) {
        return RestAssured.given().log().all()
                .pathParam("id", "1")
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("lines/{id}")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_제거_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .pathParam("id", "1")
                .when()
                .delete("/lines/{id}")
                .then().log().all()
                .extract();
        return response;
    }

    static void 지하철_노선_등록되어_있음(Map<String, String> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
