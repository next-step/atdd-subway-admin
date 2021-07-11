package nextstep.subway.line;

import static nextstep.subway.AcceptanceApi.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setStations() {
        //지하철_역_생성_요청
        지하철_역_생성_요청(makeParam("강남역"));
        지하철_역_생성_요청(makeParam("광교역"));
        지하철_역_생성_요청(makeParam("왕십리역"));
        지하철_역_생성_요청(makeParam("수원역"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(makeParam("bg-red-600", "신분당선", 1L,2L, 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 경우, 에러가 발생한다")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest params = makeParam("bg-red-600", "신분당선", 1L,2L, 10);
        지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given

        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = makeParam("bg-red-600", "신분당선", 1L,2L, 10);
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        // 지하철_노선_등록되어_있음
        LineRequest 분당선 = makeParam("bg-red-600", "분당선", 3L,4L, 10);
        ExtractableResponse<Response> 분당선_생성_응답 = 지하철_노선_생성_요청(분당선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<String> expectedLineNames = Arrays.asList(신분당선.getName(), 분당선.getName());
        List<String> resultLineNames = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());
        assertThat(resultLineNames).containsAll(expectedLineNames);

        List<Long> expectedLineIds = Stream.of(신분당선_생성_응답, 분당선_생성_응답)
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
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(makeParam("bg-red-600", "신분당선", 1L,2L, 10));
        // when
        // 지하철_노선_조회_요청
        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultStationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(1L,2L);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineNoData() {
        // given

        // when
        // 지하철_노선_조회_요청
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(makeParam("bg-red-600", "신분당선", 1L,2L, 10));

        // when
        // 지하철_노선_수정_요청
        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(id, makeParam("bg-red-600", "신분당선"));
        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(makeParam("bg-red-600", "신분당선", 1L,2L, 10));

        // when
        // 지하철_노선_제거_요청
        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private LineRequest makeParam(String color, String name, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private LineRequest makeParam(String color, String name) {
        return new LineRequest(name, color);
    }

    private StationRequest makeParam(String name) {
        return new StationRequest(name);
    }
}
