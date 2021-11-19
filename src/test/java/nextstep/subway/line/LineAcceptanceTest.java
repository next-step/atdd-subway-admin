package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
    @Override
    @Test
    public void create() {
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
    @Override
    @Test
    public void getList() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest1 = new LineRequest("1호선", "Blue");
        LineRequest lineRequest2 = new LineRequest("2호선", "Green");
        List<ExtractableResponse<Response>> givenList = givenDataList_저장한다(
            new Object[]{lineRequest1, lineRequest2}, API_URL);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 조회한다(API_URL);

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = getIdsByResponse(givenList, Long.class);
        List<Long> resultLineIds = response.jsonPath().getList("id", Long.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(resultLineIds).containsAll(expectedLineIds);
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Override
    @Test
    public void getOne() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData = givenData_저장한다(new LineRequest("1호선", "Blue"),
            API_URL);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 조회한다(givenData.header("Location"));

        // then
        // 지하철_노선_응답됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name")
                .equals(givenData.body().jsonPath().get("name"))).isTrue();
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Override
    @Test
    public void update() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData =
            givenData_저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response =
            수정한다(new LineRequest("3호선", "Orange"), givenData.header("Location"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Override
    @Test
    public void delete() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData =
            givenData_저장한다(new LineRequest("1호선", "Blue"), API_URL);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 삭제한다(givenData.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
