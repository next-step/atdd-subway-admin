package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String LINE_ONE = "1호선";
    public static final String LINE_ONE_COLOR_RED = "bg-red-600";
    public static final String LINE_TWO = "2호선";
    public static final String LINE_TWO_COLOR_GREEN = "bg-green-600";
    public static final String LINE_THREE = "3호선";
    public static final String LINE_THREE_YELLOW = "bg-yellow-600";
    public static final String ID = "id";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        LineRequest lineRequest = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> post = post(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(post.statusCode()).isEqualTo(CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse lineResponse = toLine(post(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED)));
        LineRequest lineRequest = new LineRequest(lineResponse.getName(), lineResponse.getColor());

        // when
        // 존재하는 지하철 노선 생성
        ExtractableResponse<Response> post = post(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(post.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        post(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED));
        post(new LineRequest(LINE_TWO, LINE_TWO_COLOR_GREEN));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> findResponse = get();

        // then
        assertThat(findResponse.statusCode()).isEqualTo(OK.value());
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long id = postGetId(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = get(id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED);
        Long id = postGetId(lineRequest);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateResponse = update(id, lineRequest);

        // then
        // 지하철_노선_수정됨
        LineResponse expected = toLine(updateResponse);
        assertThat(updateResponse.statusCode()).isEqualTo(CREATED.value());
        assertAll(
            () -> assertThat(expected.getName().equals(LINE_THREE)).isTrue(),
            () -> assertThat(expected.getColor().equals(LINE_THREE_YELLOW)).isTrue()
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long id = postGetId(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = delete(id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    private ExtractableResponse<Response> post(Object obj) {
        return given()
            .body(obj)
            .when().post("lines")
            .then().log().all()
            .extract();
    }

    private Long postGetId(LineRequest lineRequest) {
        return toLine(post(lineRequest)).getId();
    }

    private LineResponse toLine(ExtractableResponse<Response> resource) {
        return resource.jsonPath().getObject(".", LineResponse.class);
    }

    private ExtractableResponse<Response> get(Long id) {
        return given()
            .pathParam(ID, id)
            .get("lines/{id}")
            .then().log().all()
            .extract();
    }
    private ExtractableResponse<Response> get() {
        return given()
            .body(PageRequest.of(1, 10))
            .when().get("lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> update(Long id, Object obj) {
        return given().body(obj).pathParam(ID, id)
            .when().patch("lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> delete(Long id) {
        return given()
            .param(ID, id)
            .when().delete("lines")
            .then().log().all().extract();
    }

    private RequestSpecification given() {
        return RestAssured
            .given().log().all()
            .contentType(APPLICATION_JSON_VALUE);
    }

}
