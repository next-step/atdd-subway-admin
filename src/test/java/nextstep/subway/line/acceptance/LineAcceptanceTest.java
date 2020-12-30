package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineRequest lineRequest;
    private LineRequest lineRequest2;
    private LineRequest updatedRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        lineRequest2 = new LineRequest("구분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        updatedRequest = new LineRequest("전분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
    }

    @DisplayName("지하철 노선 생성/목록/조회/수정/삭제를 한다.")
    @Test
    void manageLine() {
        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음(lineRequest2);
        // then
        지하철_노선_생성됨(createResponse);
        지하철_노선_역_목록_조회됨(createResponse);

        // when
        ExtractableResponse<Response> failedCreateResponse = 지하철_노선_생성_요청(lineRequest);
        // then
        지하철_노선_생성_실패됨(failedCreateResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(createResponse, createResponse2));

        // when
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(createResponse);
        // then
        지하철_노선_응답됨(lineResponse);

        // when
        ExtractableResponse<Response> updatedResponse = 지하철_노선_수정_요청(createResponse, updatedRequest);
        // then
        지하철_노선_수정됨(updatedResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선_제거_요청(createResponse);
        // then
        지하철_노선_삭제됨(deleteResponse);
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return RestAssured.given().log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/lines/{lineId}", response.getId()).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> response, LineRequest lineRequest) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_역_목록_조회됨(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.as(LineResponse.class).getStations()).hasSize(2);
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
