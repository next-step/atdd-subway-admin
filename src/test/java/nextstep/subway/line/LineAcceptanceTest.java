package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("8호선", "pink", "암사역", "모란역", 15);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createDuplicateLine() {
        // given
        지하철_노선_등록되어_있음("8호선", "pink", "암사역", "모란역", 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("8호선", "pink", "잠실역", "문정역", 15);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        String createResponse1 = 지하철_노선_등록되어_있음("2호선", "green", "잠실역", "신도림역", 20);
        String createResponse2 = 지하철_노선_등록되어_있음("8호선", "pink", "암사", "모란", 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음("8호선", "pink", "암사", "모란", 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLocationUri);

        // then
        지하철_노선_응답됨(response, createdLocationUri);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음("8호선", "pink", "암사", "모란", 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdLocationUri, "신분당선", "red");

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음("8호선", "pink", "암사", "모란", 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdLocationUri);

        // then
        지하철_노선_삭제됨(response);
    }

    public ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = StationAcceptanceTest.지하철역_등록되어_있음(upStationName);
        Long downStationId = StationAcceptanceTest.지하철역_등록되어_있음(downStationName);

        LineRequest request = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        return response;
    }

    public String 지하철_노선_등록되어_있음(String name, String color, String upStationName, String downStationName, int distance) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(name, color, upStationName, downStationName, distance);
        return createResponse.header("Location");
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String uri, String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color);

        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<String> lineLocations) {
        List<Long> expectedLineIds = lineLocations.stream()
                .map(location -> Long.parseLong(location.split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, String uri) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String expectedId = uri.split("/")[2];
        assertThat(response.jsonPath().get("id").toString()).isEqualTo(expectedId);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
