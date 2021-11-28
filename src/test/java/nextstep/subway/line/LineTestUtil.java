package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.station.domain.Station;

public class LineTestUtil {

    public static final String LINES_PATH = "/lines/";

    public static LineRequest 지하철_노선_정보(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LINES_PATH)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when().get(LINES_PATH)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
            .given().log().all()
            .when().get(LINES_PATH + id)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest changeLine) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(changeLine)
            .when().put(LINES_PATH + id)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(response.header("Location"))
            .then().log().all().extract();
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> resultResponse,
        ExtractableResponse<Response> response1, ExtractableResponse<Response> response2) {
        List<Long> expectedLineIds = Arrays.asList(
            response1.jsonPath().getLong("id"),
            response2.jsonPath().getLong("id")
        );
        List<Long> resultLineIds = resultResponse.jsonPath().getList("id", Long.class);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 노선정보에_지하철역정보_포함됨(ExtractableResponse<Response> resultResponse,
        Long upStationId, Long downStationId) {
        List<Long> expectedStationIds = Arrays.asList(upStationId, downStationId);
        List<Long> resultStationIds = resultResponse.jsonPath().getList("stations", Station.class)
            .stream()
            .map(Station::getId)
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("message")).isEqualTo(
            LineDuplicateException.LINE_DUPLICATE);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_존재하지_않음(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("message")).isEqualTo(
            NotFoundLineException.NOT_FOUND_LINE);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
