package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestCommon.createResponse(params, "/lines");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600");
        LineTestCommon.createResponse(params, "/lines");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestCommon.createResponse(params, "/lines");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest param1 = LineTestCommon.createLineParams("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = LineTestCommon.createResponse(param1, "/lines");
        // 지하철_노선_등록되어_있음
        LineRequest param2 = LineTestCommon.createLineParams("2호선", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = LineTestCommon.createResponse(param2, "/lines");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineTestCommon.findAllResponse("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600");
        LineTestCommon.createResponse(params, "/lines");

        // when
        // 지하철_노선_조회_요청
        Long id = 1L;
        ExtractableResponse<Response> response = LineTestCommon.findOneResponse("/lines/" + id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest createParam = LineTestCommon.createLineParams("신분당선", "bg-red-600");
        LineTestCommon.createResponse(createParam, "/lines");
        // 지하철_노선_등록되어_있음
        LineRequest updateParam = LineTestCommon.createLineParams("구분당선", "bg-blue-600");

        // when
        // 지하철_노선_수정_요청
        Long id = 1L;
        ExtractableResponse<Response> response = LineTestCommon.updateResponse(updateParam,"/lines/" + id);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(LineResponse.class).getName()).isEqualTo(updateParam.getName());
        assertThat(response.body().as(LineResponse.class).getColor()).isEqualTo(updateParam.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = LineTestCommon.createResponse(params, "/lines");

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = LineTestCommon.deleteResponse(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
