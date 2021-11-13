package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.Fixture;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = postLines(
            lineRequest("신분당선", "bg-red-600")
        );

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_with_duplicated_name() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest lineRequest = lineRequest("신분당선", "bg-red-600");
        postLines(lineRequest);

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = postLines(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> postResponse1 = postLines(
            lineRequest("신분당선", "bg-red-600")
        );
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> postResponse2 = postLines(
            lineRequest("분당선", "bg-yellow-600")
        );

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = Fixture.get("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        final List<Long> expectedLineIds = Stream.of(postResponse1, postResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        final List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        final Long lineId = postLines(lineRequest("신분당선", "bg-red-600"))
            .as(LineResponse.class).getId();

        // when
        // 지하철_노선_조회_요청
        final ExtractableResponse<Response> response = Fixture.get("/lines/{id}", lineId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class).getId()).isEqualTo(lineId);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_notFound() {
        final ExtractableResponse<Response> response = Fixture.get("/lines/{id}", 0L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        final Long lineId = postLines(lineRequest("신분당선", "bg-red-600"))
            .as(LineResponse.class).getId();

        // when
        // 지하철_노선_수정_요청
        final ExtractableResponse<Response> response = Fixture.put(
            lineRequest("쉰분당선", "bg-magenta-600"),
            "/lines/{id}", lineId
        );

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine_notFound() {
        final ExtractableResponse<Response> response = Fixture.put(
            lineRequest("유령선", "bg-grey-600"),
            "/lines/{id}", 0L
        );
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        final Long lineId = postLines(lineRequest("신분당선", "bg-red-600"))
            .as(LineResponse.class).getId();

        // when
        // 지하철_노선_제거_요청
        final ExtractableResponse<Response> response = Fixture.delete("/lines/{id}", lineId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine_notFound() {
        final ExtractableResponse<Response> response = Fixture.delete("/lines/{id}", 0L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private LineRequest lineRequest(String name, String color) {
        return new LineRequest(name, color);
    }

    private ExtractableResponse<Response> postLines(LineRequest lineRequest) {
        return Fixture.post("/lines", lineRequest);
    }
}
