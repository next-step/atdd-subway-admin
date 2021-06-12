package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.Constants.*;
import static nextstep.subway.line.LineAcceptanceRequests.*;
import static nextstep.subway.station.StationAcceptanceRequests.requestCreateStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 지하철역 강남역 생성
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> responseStation1 = requestCreateStation(stationRequest1);
        // 지하철역 역삼역 생성
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> responseStation2 = requestCreateStation(stationRequest2);

        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR, 1L, 2L, 10);
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_error() {
        // given
        // 지하철역 강남역 등록되어 있음
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> responseStation1 = requestCreateStation(stationRequest1);
        // 지하철역 역삼역 등록되어 있음
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> responseStation2 = requestCreateStation(stationRequest2);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR, 1L, 2L, 10);
        requestCreateLine(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철역 강남역 등록되어 있음
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> responseStation1 = requestCreateStation(stationRequest1);
        // 지하철역 역삼역 등록되어 있음
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> responseStation2 = requestCreateStation(stationRequest2);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestFirst = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR, 1L, 2L, 10);
        ExtractableResponse<Response> createResponse1 = requestCreateLine(lineRequestFirst);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestSecond = new LineRequest(SECOND_LINE_COLOR, SECOND_LINE_NAME, 1L, 2L, 5);
        ExtractableResponse<Response> createResponse2 = requestCreateLine(lineRequestSecond);
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestShowLines();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
        // 지하철역이 등록되어 있음1
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = requestCreateStation(stationRequest1);
        // 지하철역이 등록되어 있음2
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = requestCreateStation(stationRequest2);

        // 지하철 노선이 등록되어 있음
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR,
                createResponse1.jsonPath().getLong("id"), createResponse2.jsonPath().getLong("id"), 10);
        ExtractableResponse<Response> createResponseLine = requestCreateLine(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestShowLine(createResponseLine.jsonPath().getLong("id"));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철역이 등록되어 있음1
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = requestCreateStation(stationRequest1);
        // 지하철역이 등록되어 있음2
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = requestCreateStation(stationRequest2);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME, 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_수정_요청
        String uri = createResponse.header("Location");
        LineRequest lineRequestOld = new LineRequest(OLD_BUNDANG_LINE_COLOR, OLD_BUNDANG_LINE_NAME, 1L, 2L, 10);
        ExtractableResponse<Response> response = requestUpdateLine(uri, lineRequestOld);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철역이 등록되어 있음1
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = requestCreateStation(stationRequest1);
        // 지하철역이 등록되어 있음2
        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = requestCreateStation(stationRequest2);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME, 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestDeleteLine(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
