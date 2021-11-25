package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_조회_요청;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_생성_요청;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_수정_요청;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_제거_요청;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_조회_요청;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_파라미터_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_노선을_생성한다() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("신분당선", "red");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("신분당선", "red");
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("2호선", "green");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(response.jsonPath().getList(".")).hasSize(2);
        assertThat(response.jsonPath().getString("name")).contains("신분당선");
        assertThat(response.jsonPath().getString("name")).contains("2호선");
    }

    @Test
    void 지하철_노선을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createLine = 지하철_노선_생성_요청("신분당선", "red");
        Long createLineId = createLine.jsonPath().getObject(".", Line.class).getId();

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createLineId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("red");
    }

    @Test
    void 지하철_노선을_수정한다() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createLine = 지하철_노선_생성_요청("신분당선", "red");
        Long createLineId = createLine.jsonPath().getObject(".", Line.class).getId();

        // when
        // 지하철_노선_수정_요청
        LineRequest updateParams = 지하철_노선_파라미터_생성("2호선", "green");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createLineId, updateParams);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).contains("2호선");
        assertThat(response.jsonPath().getString("color")).contains("green");
    }

    @Test
    void 지하철_노선을_제거한다() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createLine = 지하철_노선_생성_요청("신분당선", "red");
        Long createLineId = createLine.jsonPath().getObject(".", Line.class).getId();

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createLineId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 지하철_노선을_종점역_정보와_함께_생성한다() {
        LineRequest 지하철_노선_파라미터_생성 = 지하철_노선_파라미터_생성("신분당선", "red");
    }
}
