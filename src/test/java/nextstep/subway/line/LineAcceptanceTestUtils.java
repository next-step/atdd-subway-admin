package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;

public class LineAcceptanceTestUtils {
    private LineAcceptanceTestUtils() {
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location"))
            .isNotBlank();
    }

    public static long 지하철_노선_등록되어_있음(String name, String color, Station upStation, Station downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStation.getId()));
        params.put("downStationId", String.valueOf(downStation.getId()));
        params.put("distance", String.valueOf(distance));
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        return Long.parseLong(
            response.header("Location")
                .split("/")[2]);
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        // when
        return RestAssured
            .given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> expected) {
        Set<LineResponse> lineResponses = new HashSet<>(response.jsonPath()
            .getList(".", LineResponse.class));

        for (LineResponse lineResponse : expected) {
            assertThat(lineResponses.contains(lineResponse))
                .isTrue();
        }
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());

        assertThat(response.as(LineResponse.class))
            .isEqualTo(expected);
    }

    public static void 지하철_노선_미존재_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(long id, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(expected.getId());
        지하철_노선_응답됨(getResponse, expected);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
