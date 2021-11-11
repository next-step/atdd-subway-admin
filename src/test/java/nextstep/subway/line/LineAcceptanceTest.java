package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final int DISTANCE = 10;

    private Station pangyoStation;
    private Station jeongjaStation;

    @BeforeEach
    void beforeEach() {
        pangyoStation = 지하철_역_생성_요청(StationRequest.from("판교역")).as(Station.class);
        jeongjaStation = 지하철_역_생성_요청(StationRequest.from("정자역")).as(Station.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   jeongjaStation.getId(),
                                                   DISTANCE);
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   jeongjaStation.getId(),
                                                   DISTANCE);
        지하철_노선_등록되어_있음(lineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest firstLineRequest = LineRequest.from("신분당선",
                                                        "RED",
                                                        pangyoStation.getId(),
                                                        jeongjaStation.getId(),
                                                        DISTANCE);
        LineRequest secondLineRequest = LineRequest.from("1호선",
                                                         "BLUE",
                                                         pangyoStation.getId(),
                                                         jeongjaStation.getId(),
                                                         DISTANCE);

        ExtractableResponse<Response> firstCreateResponse = 지하철_노선_등록되어_있음(firstLineRequest);
        ExtractableResponse<Response> secondCreateResponse = 지하철_노선_등록되어_있음(secondLineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        List<Long> createLineIds = Stream.of(firstCreateResponse, secondCreateResponse)
                                         .map(this::parseIdFromLocationHeader)
                                         .collect(Collectors.toList());
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, createLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   jeongjaStation.getId(),
                                                   DISTANCE);
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        Long createdId = parseIdFromLocationHeader(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdId);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_포함됨(response, parseIdFromLocationHeader(createResponse));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   jeongjaStation.getId(),
                                                   DISTANCE);
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        Long createdId = parseIdFromLocationHeader(createResponse);
        LineRequest updateLineRequest = LineRequest.from("신분당선(수정)",
                                                         "RED(수정)",
                                                         pangyoStation.getId(),
                                                         jeongjaStation.getId(),
                                                         DISTANCE);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdId, updateLineRequest);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   jeongjaStation.getId(),
                                                   DISTANCE);
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        Long createdId = parseIdFromLocationHeader(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdId);

        // then
        지하철_노선_삭제됨(response);
    }
}
