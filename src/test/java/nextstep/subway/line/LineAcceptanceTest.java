package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URI = "lines";
    private LineRequest 일호선;
    private LineRequest 이호선;
    private static final int 거리_5 = 5;

    @Disabled
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(일호선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선과 구간을 생성한다.")
    @Test
    void createLineWithSections() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse result = response.jsonPath().getObject("", LineResponse.class);
        assertThat(result.getStations()).extracting(StationResponse::getId).contains(강남역_ID, 역삼역_ID);
        assertThat(response.header("Location")).isNotBlank();
    }

    @Disabled
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음(일호선);

        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(일호선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithExistName() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);

        RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();
        //when

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("상행 종점과 하행 종점을 같은 역으로 등록한다")
    @Test
    void createLineWithDuplicateStation() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 강남역_ID, 거리_5);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // then
        지하철_노선_생성_실패됨(response);
    }


    @Disabled
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long 일호선_ID = 지하철_노선_등록되어_있음(일호선);
        Long 이호선_ID = 지하철_노선_등록되어_있음(이호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(일호선_ID, 이호선_ID));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLinesRefact() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);
        LineRequest 사호선 = LineRequest.of("4호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);
        ExtractableResponse<Response> saveResponse = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> saveResponse2 = RestAssured
                .given().log().all()
                .body(사호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        지하철_노선_목록_응답됨(response);
        LineResponse lineResponse1 = responseLine(saveResponse);
        LineResponse lineResponse2 = responseLine(saveResponse2);
        지하철_노선_목록_포함됨(response, Arrays.asList(lineResponse1.getId(), lineResponse2.getId()));

    }

    @Disabled
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long 일호선_ID = 지하철_노선_등록되어_있음(일호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(일호선_ID);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineRefactor() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);
        ExtractableResponse<Response> saveResponse = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();
        LineResponse lineResponse = responseLine(saveResponse);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        지하철_노선_응답됨(response);
    }

    @Disabled
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long 일호선_ID = 지하철_노선_등록되어_있음(일호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(일호선_ID, 이호선);

        // then
        지하철_노선_수정됨(response, 이호선);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineRefactor() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);
        LineRequest 사호선 = LineRequest.of("4호선", "blue", 강남역_ID, 역삼역_ID, 거리_5);
        ExtractableResponse<Response> saveResponse = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();
        LineResponse lineResponse = responseLine(saveResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), 사호선);

        // then
        지하철_노선_수정됨(response, 사호선);
    }

    @Disabled
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLineRefactor() {
        // given
        Long 일호선ID = 지하철_노선_등록되어_있음(일호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(일호선ID);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLineRefa() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);
        LineRequest 삼호선 = LineRequest.of("3호선", "orange", 강남역_ID, 역삼역_ID, 거리_5);
        LineRequest 사호선 = LineRequest.of("4호선", "blue", 강남역_ID, 역삼역_ID, 거리_5);
        ExtractableResponse<Response> saveResponse = RestAssured
                .given().log().all()
                .body(삼호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(responseLine(saveResponse).getId());

        // then
        지하철_노선_삭제됨(response);
    }



    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete(BASE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(BASE_URI)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get(BASE_URI)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .when().get(BASE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(BASE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private Long 지하철_노선_등록되어_있음(LineRequest request) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);
        지하철_노선_생성됨(response);
        return responseLine(response).getId();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedIds) {
        List<Long> resultIds = responseLines(response).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(expectedIds).containsAll(resultIds);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineRequest request) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        LineResponse updatedLine = responseLine(response);
        assertAll(
                () -> assertThat(updatedLine.getColor()).isEqualTo(request.getColor()),
                () -> assertThat(updatedLine.getName()).isEqualTo(request.getName())
        );
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private LineResponse responseLine(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("", LineResponse.class);
    }

    private List<LineResponse> responseLines(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class);
    }
}
