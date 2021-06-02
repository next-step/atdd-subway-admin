package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.LineSeoul;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.RestAssuredCRUD.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private String path = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
//        LineRequest lineRequest = LineSeoul.NUMBER_2.toRequest();
        String color = "bg-red-600";
        String name = "신분당선";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = postLineRequest(path, lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

//    @DisplayName("지하철 노선을 생성한다.")
//    @Test
//    void createLine() {
//        // given
//        LineRequest lineRequest = LineSeoul.NUMBER_2.toRequest();
//
//        // when
//        // 지하철_노선_생성_요청
//        ExtractableResponse<Response> response = postLineRequest(path, lineRequest);
//
//        // then
//        // 지하철_노선_생성됨
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
//        assertThat(response.header("Location")).isNotBlank();
//    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        postLineRequest(path, LineSeoul.NUMBER_2.toRequest());

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = postLineRequest(path, LineSeoul.NUMBER_2.toRequest());

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse createResponse1 = postLineRequest(path, LineSeoul.NUMBER_2.toRequest());
        // 지하철_노선_등록되어_있음
        ExtractableResponse createResponse2 = postLineRequest(path, LineSeoul.NUMBER_6.toRequest());

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = get(path);

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse createResponse = postLineRequest(path, LineSeoul.NUMBER_2.toRequest());

        // when
        // 지하철_노선_조회_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = get(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse createResponse = postLineRequest(path, LineSeoul.NUMBER_2.toRequest());

        // when
        // 지하철_노선_수정_요청
        String uri = createResponse.header("Location");
        ExtractableResponse createResponse2 = putLineRequest(uri, LineSeoul.NUMBER_6.toRequest());

        // then
        // 지하철_노선_수정됨
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.OK.value());

        // and
        // 지하철_노선_수정확인
        LineResponse lineResponse = createResponse2.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(LineSeoul.NUMBER_6.lineName());
        assertThat(lineResponse.getColor()).isEqualTo(LineSeoul.NUMBER_6.color());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse createResponse = postLineRequest(path, LineSeoul.NUMBER_2.toRequest());

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // and
        // 지하철_노선_삭제확인
        ExtractableResponse<Response> checkResponse = get(uri);
        assertThat(checkResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


}
