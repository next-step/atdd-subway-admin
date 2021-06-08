package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineSteps {

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest request) {
        return 지하철_노선_생성_요청(request);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return sendBodyApi(request)
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(LineRequest request,
                                                             ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return sendBodyApi(request)
                .put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return getLines("/lines");
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        String uri = "/lines/" + lineId;
        return getLines(uri);
    }

    private static RequestSpecification sendBodyApi(LineRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when();
    }

    private static ExtractableResponse<Response> getLines(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
                                     List<ExtractableResponse<Response>> createdLineResponses) {
        List<Long> expectedLineIds = createdLineResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response,
                                  ExtractableResponse<Response> createdResponse) {
        LineResponse line = createdResponse.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath()
                        .getObject(".", LineResponse.class)
                        .getName()).isEqualTo(line.getName())
        );
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_지하철역_정렬된_목록_포함됨(ExtractableResponse<Response> response,
                                              List<StationResponse> stations) {
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        List<Long> expectedStationIds = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> resultStationIds = lineResponse.getStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        assertThat(resultStationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
