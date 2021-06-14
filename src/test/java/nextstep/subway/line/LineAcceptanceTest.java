package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import org.assertj.core.api.Assertions;
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

    public static final LineRequest TEST_FIRST_LINE = new LineRequest("1호선", "red");
    public static final LineRequest TEST_SECOND_LINE = new LineRequest("2호선", "blue");
    public static final int ID_POSITION = 2;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(TEST_FIRST_LINE);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청(TEST_FIRST_LINE);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(TEST_FIRST_LINE);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createdFirstLine = 지하철_노선_생성_요청(TEST_FIRST_LINE);
        ExtractableResponse<Response> createdSecondLine = 지하철_노선_생성_요청(TEST_SECOND_LINE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();


        //then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, createdFirstLine, createdSecondLine);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                    .given().log().all()
                    .when().get("/lines")
                    .then().log().all().extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdFirstLine = 지하철_노선_생성_요청(TEST_FIRST_LINE);
        long createdId = extractIdByLocationHeader(createdFirstLine);

        // when
        // 지하철_노선_조회_요청
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/" + createdId)
                .then().log().all().extract();

        // then
        지하철_노선_목록_응답됨(response);
        assertThat(createdId).isEqualTo(response.body().as(LineResponse.class).getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, ExtractableResponse<Response>...responses) {
        List<Long> expectedLineIds = extractIdsByLocationHeader(responses);

        LineResponses lines = response.body().as(LineResponses.class);
        List<Long> resultIds = lines.getLines().stream()
                .map(line -> line.getId())
                .collect(Collectors.toList());

        assertThat(resultIds).containsAll(expectedLineIds);
    }

    public static List<Long> extractIdsByLocationHeader(ExtractableResponse<Response>...responses) {
        return Arrays.asList(responses).stream()
                .map(it -> extractIdByLocationHeader(it))
                .collect(Collectors.toList());
    }

    private static long extractIdByLocationHeader(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[ID_POSITION]);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
