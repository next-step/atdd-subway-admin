package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest lineRequest2 = new LineRequest("4호선", "bg-blue-400");
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest1);
        ExtractableResponse<Response> createResponse2 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTestSupport.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(extractableResponse ->
                        Long.parseLong(extractableResponse.header(HttpHeaders.LOCATION).split("/")[2]))
                .collect(Collectors.toList());
        List<Long> lineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId).collect(Collectors.toList());
        assertThat(expectedLineIds).containsAll(lineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_조회_요청(lineRequest
                            , createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse createResponse = createResponse1.as(LineResponse.class);
        LineResponse findResponse = response.as(LineResponse.class);
        assertEquals(createResponse.getId(), findResponse.getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_수정_요청
        LineRequest modifiedLineRequest = new LineRequest("구분당선", "bg-red-600");
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_수정_요청(modifiedLineRequest
                                                , createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("ID가 없는 노선을 수정 요청 하는 경우 오류 발생")
    @Test
    void updateLineByNotExistIdOccurredException() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_수정_요청
        LineRequest modifiedLineRequest = new LineRequest("구분당선", "bg-red-600");
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_수정_요청(modifiedLineRequest
                                        , createResponse1.header(HttpHeaders.LOCATION) + "00");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestSupport.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_제거_요청(createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
