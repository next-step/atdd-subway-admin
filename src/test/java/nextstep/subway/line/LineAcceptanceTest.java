package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseSubwayTest;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.UpdateLineRequest;
import org.assertj.core.api.AbstractIntegerAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;


@DisplayName("지하철 노선 관련 기능")
@DirtiesContext
class LineAcceptanceTest extends BaseSubwayTest {

    private static final String LINES_PATH = "/lines";

    @Autowired
    LineRepository lineRepository;

    @AfterEach
    void tearDown() {
        lineRepository.deleteAll();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철노선_생성")
    void createLine() {
        // when
        지하철노선_생성(LineRequest.of("신분당선", "bg-red-600", 1, 2, 10));

        // then
        final ExtractableResponse<Response> actual = 지하철노선_목록조회();

        assertThat(actual.body().jsonPath().getList("name")).contains("신분당선");

    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록 조회")
    void getLines() {
        // given
        지하철노선_생성(LineRequest.of("신분당선", "bg-red-600", 1, 2, 10));
        지하철노선_생성(LineRequest.of("분당선", "bg-green-600", 1, 3, 10));

        // when
        final ExtractableResponse<Response> actual = 지하철노선_목록조회();

        // then
        assertAll(
                () -> 성공_응답_검증(actual),
                () -> assertThat(actual.body().jsonPath().getList("$")).hasSize(2),
                () -> assertThat(actual.body().jsonPath().getList("name")).contains("신분당선", "분당선")
        );

    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선 조회")
    void getLine() {
        // given
        final ExtractableResponse<Response> line = 지하철노선_생성(LineRequest.of("신분당선", "bg-red-600", 1, 2, 10));

        // when
        final ExtractableResponse<Response> actual = 지하철노선_조회(아이디찾기(line));

        // then
        assertAll(
                () -> 성공_응답_검증(actual),
                () -> assertThat(actual.body().jsonPath().getString("name")).isEqualTo("신분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철노선 수정")
    void updateLine() {
        // given
        final ExtractableResponse<Response> line = 지하철노선_생성(LineRequest.of("신분당선", "bg-red-600", 1, 2, 10));
        final long id = 아이디찾기(line);

        // when
        final ExtractableResponse<Response> updateResponse = 지하철노선_수정(id, UpdateLineRequest.of("다른분당선", "bg-red-600"));

        final ExtractableResponse<Response> getResponse = 지하철노선_조회(id);

        // then
        assertAll(
                () -> 성공_응답_검증(updateResponse),
                () -> assertThat(getResponse.body().jsonPath().getString("name")).isEqualTo("다른분당선"),
                () -> assertThat(getResponse.body().jsonPath().getString("color")).isEqualTo("bg-red-600")
        );

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선 삭제")
    void deleteLine() {
        // given
        final ExtractableResponse<Response> line = 지하철노선_생성(LineRequest.of("신분당선", "bg-red-600", 1, 2, 10));

        // when
        final ExtractableResponse<Response> deleteResponse = 지하철노선_삭제(아이디찾기(line));

        // then
        assertAll(
                () -> 성공_빈_내용_검증(deleteResponse),
                () -> assertThat(지하철노선_목록조회().body().jsonPath().getList("$")).hasSize(0)
        );
    }

    private ExtractableResponse<Response> 지하철노선_생성(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(lineRequest, ObjectMapperType.JACKSON_2)
                .post(LINES_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_목록조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINES_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회(final long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINES_PATH + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정(final long id, final UpdateLineRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request, ObjectMapperType.JACKSON_2)
                .put(LINES_PATH + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제(final long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINES_PATH + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private long 아이디찾기(final ExtractableResponse<Response> line) {
        return line.body().jsonPath().getLong("id");
    }

    private AbstractIntegerAssert<?> 성공_응답_검증(final ExtractableResponse<Response> updateResponse) {
        return assertThat(updateResponse.statusCode()).isEqualTo(OK.value());
    }

    private AbstractIntegerAssert<?> 성공_빈_내용_검증(final ExtractableResponse<Response> deleteResponse) {
        return assertThat(deleteResponse.statusCode()).isEqualTo(NO_CONTENT.value());
    }
}
