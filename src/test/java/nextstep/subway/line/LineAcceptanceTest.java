package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue", 1L, 2L, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("잠실역", "Green", 1L, 2L, 10);
        지하철_노선_등록(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLines() {
        return Stream.of(
                // given
                // 지하철_노선_등록되어_있음
                DynamicTest.dynamicTest("여러 노선을 생성한다.", () -> {
                    //when
                    ExtractableResponse<Response> createFirstLine = 지하철_노선_등록(new LineRequest("1호선", "blue", 1L, 2L, 10));
                    ExtractableResponse<Response> createSecondLine = 지하철_노선_등록(new LineRequest("2호선", "green", 1L, 2L, 10));
                    ExtractableResponse<Response> createThirdLine = 지하철_노선_등록(new LineRequest("3호선", "orange", 1L, 2L, 10));

                    //then
                    assertThat(createFirstLine.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(createSecondLine.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    ExtractableResponse<Response> searchResponse = 지하철_목록_조회();
                    assertThat(searchResponse.jsonPath().getList(".", LineResponse.class).size()).isEqualTo(3);
                }),

                // then
                // 지하철_노선_목록_응답됨
                // 지하철_노선_목록_포함됨
                DynamicTest.dynamicTest("지하철 노선 목록에 포함되어 있는지 확인한다.", () -> {
                    //given
                    ExtractableResponse<Response> searchResponse = 지하철_목록_조회();

                    //when
                    List<String> searchLineNames = searchResponse.jsonPath().getList("name");

                    //then
                    assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(searchLineNames.contains("1호선")).isTrue();
                    assertThat(searchLineNames.contains("2호선")).isTrue();
                })
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> searchLine = 지하철_노선_조회(1L);

        // then
        // 지하철_노선_응답됨
        LineResponse line = searchLine.jsonPath().getObject(".", LineResponse.class);
        assertThat(searchLine.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getSections().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록(new LineRequest("화곡역", "Purple", 1L, 2L, 10));
        // 지하철_노선_조회
        ExtractableResponse<Response> response = 지하철_노선_조회(1L);
        // 지하철_노선_응답됨
        LineResponse line = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(line.getName()).isEqualTo("화곡역");
        assertThat(line.getColor()).isEqualTo("Purple");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정(new LineRequest("화곡역", "Green", 1L, 2L, 10), line.getId());
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> searchUpdateLine = 지하철_노선_조회(1L);
        LineResponse expected = searchUpdateLine.jsonPath().getObject(".", LineResponse.class);
        assertThat(expected.getName()).isEqualTo("화곡역");
        assertThat(expected.getColor()).isEqualTo("Green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록(new LineRequest("화곡역", "Purple", 1L, 2L, 10));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> deleteResponse = 지하철_노선_제거(1L);

        // then
        // 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_목록_조회() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_수정(LineRequest lineRequest, Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_제거(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
