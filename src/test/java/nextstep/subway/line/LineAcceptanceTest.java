package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.station.StationAcceptanceTest.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    //given
    public LineRequest 수인분당선;
    public LineRequest 신분당선;
    private StationResponse 강남역;
    private StationResponse 역삼역;

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    @BeforeEach
    void beforeEach() {
        강남역 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = 지하철_역_등록되어_있음("역삼역").as(StationResponse.class);

        수인분당선 = new LineRequest("수인분당선", "yellow", 강남역.getId(), 역삼역.getId(), 10);
        신분당선 = new LineRequest("신분당선", "red", 강남역.getId(), 역삼역.getId(), 15);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(수인분당선);

        // then
        지하철_노선_생성됨(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.CREATED);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createDuplicateLine() {
        // given
        지하철_노선_등록되어_있음(수인분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(수인분당선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createLine1 = 지하철_노선_등록되어_있음(수인분당선);
        ExtractableResponse<Response> createLine2 = 지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Stream.of(createLine1, createLine2));
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.OK);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, Stream<ExtractableResponse<Response>> created) {
        List<Long> expectedLineIds = created.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createLine = 지하철_노선_등록되어_있음(수인분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createLine);

        // then
        지하철_노선_응답됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().get(createResponse.header("Location"))
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.OK);
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createLine = 지하철_노선_등록되어_있음(수인분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createLine.header("Location"), 신분당선);

        // then
        지하철_노선_수정됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String location, LineRequest updatedLineRequest) {
        return RestAssured
                .given().log().all()
                .body(updatedLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(location)
                .then().log().all().extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.OK);
    }

    @DisplayName("존재하지 않는 아이디로 지하철 노선을 수정한다.")
    @Test
    void updateNotFoundLine() {
        // when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_수정_요청(신분당선);

        // then
        지하철_노선_수정_실패(response);
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_수정_요청(LineRequest updatedLineRequest) {
        return 지하철_노선_수정_요청("/lines/1", updatedLineRequest);
    }

    private void 지하철_노선_수정_실패(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createLine = 지하철_노선_등록되어_있음(수인분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createLine.header("Location"));

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String location) {
        return RestAssured
                .given().log().all()
                .when().delete(location)
                .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.NO_CONTENT);
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteNotFoundLine() {
        // when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_제거_요청();

        // then
        지하철_노선_삭제_실패(response);
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_제거_요청() {
        return 지하철_노선_제거_요청("/lines/1");
    }

    private void 지하철_노선_삭제_실패(ExtractableResponse<Response> response) {
        요청_결과_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("노선 등록시 구간정보도 같이 저장한다.")
    @Test
    void createLineAndSection() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}