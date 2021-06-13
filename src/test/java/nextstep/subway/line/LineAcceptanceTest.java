package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest lineRequest;

    @BeforeEach
    public void setup() {
        lineRequest = new LineRequest("4호선", "blue", getStationId("서울역"), getStationId("안산역"), 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);

        //then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //when
        지하철_노선_생성(lineRequest);
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);

        //then
        지하철_노선_존재함(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성(lineRequest);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성(new LineRequest("2호선", "green", getStationId("강남역"), getStationId("잠실역"), 10));

        //when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //then
        지하철_노선_목록_조회됨(response);
        지하철_노선_목록_포함됨(Arrays.asList(createResponse1, createResponse2), response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        String createdLocationUri = 지하철_노선_등록되어_있음(lineRequest);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회(createdLocationUri);

        //then
        지하철_노선_조회됨(createdLocationUri, response);
    }

    @DisplayName("지하철 노선을 조회 실패")
    @Test
    void getLineFail() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/1");

        //then
        지하철_노선_존재하지_않음(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        String changeName = "2호선";
        String changeColor = "green";

        //when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(changeName, changeColor);

        //then
        지하철_노선_수정됨(changeName, changeColor, response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        //given
        String createdLocationUri = 지하철_노선_등록되어_있음(lineRequest);

        //when
        ExtractableResponse<Response> response = 지하철_노선_삭제(createdLocationUri);

        //then
        지하철_노선_삭제됨(createdLocationUri, response);
    }

    private static ExtractableResponse<Response> 지하철_노선_생성(LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_존재함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String createdLocationUri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().jsonPath().getString("stations")).isNotBlank();
    }

    public static String 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성(lineRequest).header("Location");
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String changeName, String changeColor) {
        LineRequest line = new LineRequest(changeName, changeColor);
        String createdLocationUri = 지하철_노선_등록되어_있음(lineRequest);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(line)
                .when()
                .put(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(String createdLocationUri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(createdLocationUri)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(List<ExtractableResponse<Response>> createResponses, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = createResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_조회됨(String createdLocationUri, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(createdLocationUri.split("/")[2]);
    }

    private void 지하철_노선_존재하지_않음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철_노선_수정됨(String changeName, String changeColor, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(changeName);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(changeColor);
    }

    private void 지하철_노선_삭제됨(String createdLocationUri, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(지하철_노선_조회(createdLocationUri).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static long getStationId(String stationName) {
        return Long.parseLong(지하철_역_생성되어_있음(stationName).split("/")[2]);
    }

}
