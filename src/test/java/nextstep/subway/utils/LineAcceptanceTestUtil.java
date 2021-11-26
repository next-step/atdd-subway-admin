package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestUtil {

    private LineAcceptanceTestUtil() {
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String lineName, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = 지하철_노선_생성_파라미터_맵핑(lineName, color, upStationId, downStationId,
            distance);
        return 지하철_노선_생성_요청(params);
    }

    public static Long 지하철_노선_등록되어_있음_toId(String lineName, String color, Long upStationId,
        Long downStationId, int distance) {
        Map<String, String> params = 지하철_노선_생성_파라미터_맵핑(lineName, color, upStationId, downStationId,
            distance);
        return Long.parseLong(지하철_노선_생성_요청(params).header("Location").split("/")[2]);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> updateParams,
        Long lineId) {
        return RestAssured.given().log().all()
            .body(updateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + lineId)
            .then()
            .log().all()
            .extract();
    }


    public static Map<String, String> 지하철_노선_생성_파라미터_맵핑(String lineName, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        return params;
    }

    public static Map<String, String> 지하철_노선_생성_파라미터_맵핑(String lineName, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static List<Long> ids_추출_ByLineResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    public static void 노선_등록_실패(ExtractableResponse<Response> lineCreateResponse,
        HttpStatus httpStatus) {
        assertThat(lineCreateResponse.statusCode()).isEqualTo(httpStatus.value());
    }

    public static void 노선_조회_노선ID_포함됨(ExtractableResponse<Response> response,
        List<Long> expectedLineIds) {
        List<Long> 결과등록노선ID목록 = ids_추출_ByLineResponse(response);

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(결과등록노선ID목록).containsAll(expectedLineIds)
        );
    }

    public static void 노선_수정_성공(Map<String, String> updateParams,
        ExtractableResponse<Response> updateResponse) {
        String 수정된노선이름 = updateResponse.jsonPath().get("name");
        assertAll(
            () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(수정된노선이름).isEqualTo(updateParams.get("name"))
        );
    }
}
