package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.utils.RestAssuredUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    public static final LineRequest CREATE_LINE = new LineRequest("2호선", "red lighten-2");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성(CREATE_LINE);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(createResponse);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(final LineRequest lineRequest) {
        return RestAssuredUtils.post(lineRequest, "/lines").extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_및_검증(final LineRequest lineRequest) {
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성(lineRequest);

        지하철_노선_생성됨(createResponse);

        return createResponse;
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> createResponse) {
        assertAll(
            () -> 응답코드_검증(createResponse, HttpStatus.SC_CREATED),
            () -> assertThat(로케이션_가져오기(createResponse)).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_및_검증(CREATE_LINE);

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> duplicatedCreateResponse = 지하철_노선_생성(CREATE_LINE);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(duplicatedCreateResponse);
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> createResponse) {
        응답코드_검증(createResponse, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_및_검증(CREATE_LINE);

        // 지하철_노선_등록되어_있음
        final LineRequest otherCreateLine = new LineRequest("3호선", "red lighten-3");
        final ExtractableResponse<Response> otherCreateResponse = 지하철_노선_생성_및_검증(otherCreateLine);

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = 지하철_노선_목록조회();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);

        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(createResponse, otherCreateResponse, response);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록조회() {
        return RestAssuredUtils.get("/lines").extract();
    }

    public static void 지하철_노선_목록_응답됨(final ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_OK);
    }

    public static void 지하철_노선_목록_포함됨(final ExtractableResponse<Response> createResponse,
        final ExtractableResponse<Response> otherCreateResponse, final ExtractableResponse<Response> response) {
        final List<Long> expectedLineIds = Stream.of(createResponse, otherCreateResponse)
            .map(AcceptanceTest::아이디_추출하기)
            .collect(Collectors.toList());

        final List<Long> resultLineIds = response.as(LineResponses.class).getLineResponses().stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_및_검증(CREATE_LINE);

        // when
        // 지하철_노선_조회_요청
        final String uri = 로케이션_가져오기(createResponse);
        final ExtractableResponse<Response> response = 지하철_노선_조회(uri);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(uri, response);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(final String uri) {
        return RestAssuredUtils.get(uri).extract();
    }

    public static void 지하철_노선_응답됨(final String uri, final ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_OK);

        final LineResponse lineResponse = response.as(LineResponse.class);
        final long expectedLineId = 아이디_추출하기(uri);
        final long resultLineId = lineResponse.getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_및_검증(CREATE_LINE);

        // when
        // 지하철_노선_수정_요청
        final String updateName = "3호선";
        final String updateColor = "red";
        final LineRequest updateLine = new LineRequest(updateName, updateColor);

        final String uri = 로케이션_가져오기(createResponse);
        final ExtractableResponse<Response> response = 지하철_노선_수정(updateLine, uri);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(updateName, updateColor, response);
    }

    private ExtractableResponse<Response> 지하철_노선_수정(LineRequest updateLine, String uri) {
        return RestAssuredUtils.put(updateLine, uri).extract();
    }

    private void 지하철_노선_수정됨(final String updateName, final String updateColor,
        final ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_OK);
        assertThat(response.as(LineResponse.class)).isNotNull()
            .extracting(LineResponse::getName, LineResponse::getColor)
            .containsExactly(updateName, updateColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_및_검증(CREATE_LINE);

        // when
        // 지하철_노선_제거_요청
        final String uri = 로케이션_가져오기(createResponse);
        final ExtractableResponse<Response> response = 지하철_노선_제거(uri);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거(String uri) {
        return RestAssuredUtils.delete(uri).extract();
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_NO_CONTENT);
    }
}
