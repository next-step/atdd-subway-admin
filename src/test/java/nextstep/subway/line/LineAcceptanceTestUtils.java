package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;

public class LineAcceptanceTestUtils {
    private LineAcceptanceTestUtils() {
    }

    @DisplayName("지하철_노선_생성_요청")
    public static ExtractableResponse<Response> requestToCreateLine(Map<String, String> params) {
        // when
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_생성됨")
    public static void createLineSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철_노선_등록되어_있음")
    public static long lineCreated(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        ExtractableResponse<Response> response = requestToCreateLine(params);
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("지하철_노선_생성_실패됨")
    public static void createLineFail(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철_노선_목록_조회_요청")
    public static ExtractableResponse<Response> requestToCreateLineList() {
        // when
        return RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_목록_응답됨")
    public static void createLineListSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철_노선_목록_포함됨")
    public static void lineListContainsExpected(ExtractableResponse<Response> response, List<LineResponse> expected) {
        Set<LineResponse> lineResponses = new HashSet<>(response.jsonPath().getList(".", LineResponse.class));

        for (LineResponse lineResponse : expected) {
            assertThat(lineResponses.contains(lineResponse)).isTrue();
        }
    }

    @DisplayName("지하철_노선_조회_요청")
    public static ExtractableResponse<Response> reqeustToGetLine(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_응답됨")
    public static void getLineSuccess(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isEqualTo(expected);
    }

    @DisplayName("지하철_노선_미존재_응답됨")
    public static void getLineNotFound(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철_노선_수정_요청")
    public static ExtractableResponse<Response> requestToUpdateLine(long id, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_수정됨")
    public static void updateLineSuccess(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = reqeustToGetLine(expected.getId());
        getLineSuccess(getResponse, expected);
    }

    @DisplayName("지하철_노선_제거_요청")
    public static ExtractableResponse<Response> requestToDeleteLine(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_삭제됨")
    public static void lineDeleteSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
