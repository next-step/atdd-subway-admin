package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponse.StationResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineFixture {

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(final LineRequest request) {
        return given().log().all()
                .body(request)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(final String uri) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(final String uri, final LineRequest request) {
        return given().log().all()
                .body(request)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(final String uri) {
        return given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, final SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    public static void 지하철_노선_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    public static void 지하철_노선_조회_실패_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void 지하철_노선_목록_포함됨(final ExtractableResponse<Response> response,
                                     final ExtractableResponse<Response>... createdResponses) {
        LineResponse[] lineResponses = Arrays.stream(createdResponses)
                .map(createdResponse -> createdResponse.as(LineResponse.class))
                .toArray(LineResponse[]::new);
        assertThat(response.as(LineResponse[].class)).contains(lineResponses);
    }

    public static void 지하철_노선_생성_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선_값_검증됨(final ExtractableResponse<Response> response, final String name, final String color) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo(name),
                () -> assertThat(lineResponse.getColor()).isEqualTo(color),
                () -> assertThat(lineResponse.getModifiedDate()).isNotNull(),
                () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
                () -> assertThat(lineResponse.getStations()).isNotNull(),
                () -> assertThat(lineResponse.getStations().size()).isGreaterThanOrEqualTo(1)
        );
    }

    public static void 지하철_노선_삭제됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_생성_실패됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선에_지하철역_포함됨(final ExtractableResponse<Response> response, final Long... stationIds) {
        List<Long> ids = response.jsonPath()
                .getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(ids).contains(stationIds);
    }
}
