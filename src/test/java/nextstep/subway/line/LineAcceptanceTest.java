package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.station.StationAcceptanceStep.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));
        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(수도권_신분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));
        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);
        지하철_노선_등록되어_있음(수도권_신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(수도권_신분당선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));

        Long stationId3 = 지하철_역_등록되어_있음(new StationRequest("당산역"));
        Long stationId4 = 지하철_역_등록되어_있음(new StationRequest("신도림"));

        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);
        LineRequest 수도권_2호선 = LineRequest.of("수도권_2호선", "bg-green-600", stationId3, stationId4, 3);

        Long createdLineId1 = 지하철_노선_등록되어_있음(수도권_신분당선);
        Long createdLineId2 = 지하철_노선_등록되어_있음(수도권_2호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(createdLineId1, createdLineId2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));
        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);

        Long createdLineId = 지하철_노선_등록되어_있음(수도권_신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLineId);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));

        Long stationId3 = 지하철_역_등록되어_있음(new StationRequest("당산역"));
        Long stationId4 = 지하철_역_등록되어_있음(new StationRequest("신도림"));

        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);
        LineRequest 수도권_2호선 = LineRequest.of("수도권_2호선", "bg-green-600", stationId3, stationId4, 3);

        Long createdLineId = 지하철_노선_등록되어_있음(수도권_신분당선);

        // when
        ExtractableResponse response = 지하철_노선_수정_요청(createdLineId, 수도권_2호선);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Long stationId1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        Long stationId2 = 지하철_역_등록되어_있음(new StationRequest("판교역"));
        LineRequest 수도권_신분당선 = LineRequest.of("수도권_신분당선", "bg-red-600", stationId1, stationId2, 5);

        Long createdLineId = 지하철_노선_등록되어_있음(수도권_신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdLineId);

        // then
        지하철_노선_삭제됨(response);
    }
}
