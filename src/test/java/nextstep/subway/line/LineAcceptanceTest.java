package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LOCATION = "Location";
    private static final String BASE_URI = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = requestCreateLine("신분당선", "bg-red-600");

        // then
        assertCreateLineSuccess(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        final String lineName = "신분당선";
        final String lineColor = "bg-red-600";
        registerLine(lineName, lineColor);

        // when
        final ExtractableResponse<Response> response = requestCreateLine(lineName, lineColor);

        // then
        assertCreateLineFail(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final String uri1 = registerLine("신분당선", "bg-red-600");
        final String uri2 = registerLine("2호선", "bg-green-600");

        // when
        final ExtractableResponse<Response> response = requestGetLines();

        // then
        assertGetLinesSuccess(response);
        assertGetLinesContained(response, Arrays.asList(uri1, uri2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final String uri = registerLine("신분당선", "bg-red-600");

        // when
        final ExtractableResponse<Response> response = requestGetLine(uri);

        // then
        assertGetLineSuccess(response);
        assertGetLineFetched(response, uri);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_notFound() {
        // when
        final ExtractableResponse<Response> response = requestGetLine(BASE_URI + "/1");

        // then
        assertGetLineNotFound(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final String uri = registerLine("신분당선", "bg-red-600");

        // when
        final String newLineName = "구분당선";
        final String newLineColor = "bg-blue-600";
        final ExtractableResponse<Response> response = requestUpdateLine(newLineName, newLineColor,
            uri);

        // then
        assertGetLineSuccess(response);
        assertUpdateLineSuccess(uri, newLineName, newLineColor);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine_notFound() {
        // when
        final ExtractableResponse<Response> response = requestUpdateLine(
            "신분당선",
            "bg-red-600",
            BASE_URI + "/1"
        );

        // then
        assertGetLineNotFound(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final String uri = registerLine("신분당선", "bg-red-600");

        // when
        final ExtractableResponse<Response> response = requestDeleteLine(uri);

        // then
        assertDeleteLineSuccess(response);
    }

    private String registerLine(final String name, final String color) {
        final LineRequest request = new LineRequest(name, color);
        return postLineRequest(request).header(LOCATION);
    }

    private ExtractableResponse<Response> requestCreateLine(final String name, final String color) {
        final LineRequest request = new LineRequest(name, color);
        return postLineRequest(request);
    }

    private ExtractableResponse<Response> postLineRequest(final LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(BASE_URI)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestGetLines() {
        return RestAssured.given().log().all()
            .when()
            .get(BASE_URI)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestGetLine(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(
        final String name,
        final String color,
        final String uri
    ) {
        final LineRequest updateRequest = new LineRequest(name, color);
        return RestAssured.given().log().all()
            .body(updateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private void assertCreateLineSuccess(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.CREATED);
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    private void assertCreateLineFail(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.BAD_REQUEST);
    }

    private void assertGetLinesSuccess(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.OK);
    }

    private void assertGetLinesContained(
        final ExtractableResponse<Response> response,
        final List<String> uris
    ) {
        final List<Long> expectedLineIds = uris.stream()
            .map(it -> Long.parseLong(it.split("/")[2]))
            .collect(Collectors.toList());
        final List<Long> actualLineIds = response.jsonPath()
            .getList("id", Long.class);
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    private void assertGetLineSuccess(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.OK);
    }

    private void assertGetLineFetched(
        final ExtractableResponse<Response> response,
        final String expectedUri
    ) {
        final Long actualLineId = response.jsonPath()
            .getObject(".", LineResponse.class)
            .getId();
        final Long expectedLineId = Long.parseLong(expectedUri.split("/")[2]);
        assertThat(actualLineId).isEqualTo(expectedLineId);
    }

    private void assertGetLineNotFound(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.NOT_FOUND);
    }

    private void assertUpdateLineSuccess(final String uri, final String newLineName,
        final String newLineColor) {
        final LineResponse actualLine = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract()
            .jsonPath()
            .getObject(".", LineResponse.class);
        assertAll(
            () -> assertThat(actualLine.getId()).isEqualTo(Long.parseLong(uri.split("/")[2])),
            () -> assertThat(actualLine.getName()).isEqualTo(newLineName),
            () -> assertThat(actualLine.getColor()).isEqualTo(newLineColor)
        );
    }

    private void assertDeleteLineSuccess(final ExtractableResponse<Response> response) {
        assertResponseStatusEquals(response, HttpStatus.NO_CONTENT);
    }

    private void assertResponseStatusEquals(
        final ExtractableResponse<Response> response,
        final HttpStatus httpStatus
    ) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }
}
