package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.ui.LineControllerTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철_노선_생성_성공")
    @Test
    void 지하철_노선_생성_성공() {
        // When
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 지하철_중복_노선_생성_예외() {
        // given
        ExtractableResponse<Response> firstResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000"));

        // when
        ExtractableResponse<Response> secondResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000"));

        // then
        assertThat(secondResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철_노선_목록_조회_성공")
    @Test
    void 지하철_노선_목록_조회_성공() {
        // given
        LineRequest firstRequest = new LineRequest("1호선", "FF0000");
        LineRequest secondRequest = new LineRequest("2호선", "00FF00");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(firstRequest);
        ExtractableResponse createResponse2 = 지하철_노선_생성_요청(secondRequest);

        // when
        ExtractableResponse response = 지하철_노선_목록_조회_요청();

        // then
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        System.out.println("resultLineIds" + resultLineIds);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철_노선_목록_조회_성공_데이터없음")
    @Test
    void 지하철_노선_목록_조회_성공_데이터없음() {
        // when
        ExtractableResponse response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철_노선_조회_성공")
    @Test
    void 지하철_노선_조회_성공() {
        // given
        LineRequest firstRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(firstRequest);
        Long savedId = Long.valueOf(createResponse1.header("Location").split("/")[2])

        // when
        ExtractableResponse response = 지하철_노선_조회_요청(savedId);
        LineResponse expectedResult = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isEqualTo(createResponse1.body());
    }

    @DisplayName("지하철_노선_검색_성공")
    @Test
    void 지하철_노선_검색_성공() {
        // given
        LineRequest firstRequest = new LineRequest("1호선", "FF0000");
        LineRequest secondRequest = new LineRequest("2호선", "00FF00");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(firstRequest);
        ExtractableResponse createResponse2 = 지하철_노선_생성_요청(secondRequest);

        // when
        ExtractableResponse response = 지하철_노선_검색_요청(firstRequest.getName());
        LineResponse expectedResult = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isEqualTo(createResponse1.body());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선_수정_성공() {
        // given
        LineRequest saveRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(saveRequest);

        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse updateResponse = 지하철_노선_수정_요청(updateRequest);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.jsonPath().<String>get("$.name")).isEqualTo(updateRequest.getName());
        assertThat(updateResponse.jsonPath().<String>get("$.color")).isEqualTo(updateRequest.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void 지하철_노선_제거_성공() {
        // given
        LineRequest saveRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(saveRequest);

        // when
        LineRequest updateRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse updateResponse = 지하철_노선_삭제_요청(updateRequest);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
