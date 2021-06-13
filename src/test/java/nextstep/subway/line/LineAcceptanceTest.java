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
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @DisplayName("지하철_중복_노선_생성_예외_중복된_이름")
    @Test
    void 지하철_중복_노선_생성_예외_중복된_이름() {
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

    @DisplayName("지하철_노선_검색_성공")
    @Test
    void 지하철_노선_검색_성공() {
        // given
        LineRequest firstRequest = new LineRequest("1호선", "FF0000");
        LineRequest secondRequest = new LineRequest("2호선", "00FF00");
        LineRequest thirdRequest = new LineRequest("3호선", "00FF00");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(firstRequest);
        ExtractableResponse createResponse2 = 지하철_노선_생성_요청(secondRequest);
        ExtractableResponse createResponse3 = 지하철_노선_생성_요청(thirdRequest);
        List<Long> expectedResult = Arrays.asList(createResponse2, createResponse3).stream()
                .map(it -> it.jsonPath().getObject(".", LineResponse.class).getId())
                .collect(Collectors.toList());

        // when
        ExtractableResponse response = 지하철_노선_검색_요청(new LineRequest("", "00FF00"));
        List<Long> actualResult = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResult).containsAll(expectedResult);
    }

    @DisplayName("지하철_노선_검색_성공_데이터없음")
    @Test
    void 지하철_노선_검색_성공_데이터없음() {
        // when
        ExtractableResponse response = 지하철_노선_검색_요청(new LineRequest("", "00FF00"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철_노선_PK_조건_조회_성공")
    @Test
    void 지하철_노선_PK_조건_조회_성공() {
        // given
        LineRequest firstRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse = 지하철_노선_생성_요청(firstRequest);
        LineResponse expectedResult = createResponse.jsonPath().getObject(".", LineResponse.class);
        Long savedId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse response = 지하철_노선_PK_조건_조회_요청(savedId);
        LineResponse actualResult = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertAll(
                () -> assertThat(actualResult.getId()).isEqualTo(expectedResult.getId()),
                () -> assertThat(actualResult.getName()).isEqualTo(expectedResult.getName()),
                () -> assertThat(actualResult.getColor()).isEqualTo(expectedResult.getColor())
        );
    }

    @DisplayName("지하철_노선_PK_조건_조회_성공_데이터없음")
    @Test
    void 지하철_노선_PK_조건_조회_성공_데이터없음() {
        // given
        Long targetId = Long.MAX_VALUE;

        // when
        ExtractableResponse response = 지하철_노선_PK_조건_조회_요청(targetId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철_노선_수정_성공")
    @Test
    void 지하철_노선_수정_성공() {
        // given
        LineRequest saveRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse = 지하철_노선_생성_요청(saveRequest);
        Long savedId = createResponse.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse updateResponse = 지하철_노선_수정_요청(savedId, updateRequest);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updateResponse.jsonPath().<String>get("name")).isEqualTo(updateRequest.getName());
        assertThat(updateResponse.jsonPath().<String>get("color")).isEqualTo(updateRequest.getColor());
    }

    @DisplayName("지하철_노선_수정_예외_존재하지_않는_PK")
    @Test
    void 지하철_노선_수정_예외_존재하지_않는_PK() {
        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse updateResponse = 지하철_노선_수정_요청(Long.MAX_VALUE, updateRequest);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철_노선_수정_예외_중복된_이름")
    @Test
    void 지하철_노선_수정_예외_수정_중복된_이름() {
        // given
        LineRequest saveRequest1 = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse1 = 지하철_노선_생성_요청(saveRequest1);
        Long savedId1 = createResponse1.jsonPath().getObject(".", LineResponse.class).getId();

        LineRequest saveRequest2 = new LineRequest("2호선", "00FF00");
        ExtractableResponse createResponse2 = 지하철_노선_생성_요청(saveRequest2);
        Long savedId2 = createResponse2.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse updateResponse = 지하철_노선_수정_요청(savedId2, updateRequest);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철_노선_제거_성공")
    @Test
    void 지하철_노선_제거_성공() {
        // given
        LineRequest saveRequest = new LineRequest("1호선", "FF0000");
        ExtractableResponse createResponse = 지하철_노선_생성_요청(saveRequest);
        Long savedId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse deleteResponse = 지하철_노선_삭제_요청(savedId);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @DisplayName("지하철_노선_제거_예외_존재하지_않는_PK")
    @Test
    void 지하철_노선_제거_예외_존재하지_않는_PK() {
        // when
        ExtractableResponse deleteResponse = 지하철_노선_삭제_요청(Long.MAX_VALUE);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
