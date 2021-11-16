package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String API_URL = "lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "Blue");

        // when
        ExtractableResponse<Response> response = 저장한다(lineRequest, API_URL);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(response.body().jsonPath().get("name").equals(lineRequest.getName()))
                .isTrue();
        });
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
            저장한다(new LineRequest("1호선", "Orange"), API_URL);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 =
            저장한다(new LineRequest("1호선", "Blue"), API_URL);
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 =
            저장한다(new LineRequest("2호선", "Green"), API_URL);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 조회한다(API_URL);

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<Long> expectedLineIds = getIdsByResponse(
                Arrays.asList(createResponse1, createResponse2), Long.class);
            List<Long> resultLineIds = response.jsonPath().getList("id", Long.class);
            assertThat(resultLineIds).containsAll(expectedLineIds);
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse =
            저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 조회한다(createResponse.header("Location"));

        // then
        // 지하철_노선_응답됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name")
                .equals(createResponse.body().jsonPath().get("name"))).isTrue();
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse =
            저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response =
            수정한다(new LineRequest("3호선", "Orange"), createResponse.header("Location"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse =
            저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = 삭제한다(createResponse.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
