package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.step.LineAcceptanceStep.*;
import static nextstep.subway.station.step.StationAcceptanceStep.CREATED_STATION;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("상행종점, 하행좀점을 포함해서 지하철 노선을 생성한다.")
    @Test
    void createLine() {
        String lineName = "비 내리는 호남선";
        String lineColor = "남행열차색";
        Long distance = 5L;
        // given
        // 상행종점역이 생성되어 있다.
        // and 하행종점역이 생성되어 있다.
        ExtractableResponse<Response> upStationCreated = CREATED_STATION(new StationRequest("갱남역"));
        ExtractableResponse<Response> downStationCreated = CREATED_STATION(new StationRequest("서초역"));

        // when
        // 지하철 노선 생성 요청
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreated);
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationCreated);
        LineRequest lineRequest = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);

        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(lineRequest);

        // then
        // 지하철 노선 생성됨
        LINE_CREATE_SUCCESS(response, lineName, upStationId, downStationId);
    }

    @DisplayName("존재하지 않는 역을 상행 종점역이나 하행 종점역으로 지정하고 지하철 노선을 생성한다.")
    @Test
    void createLineWithoutStation() {
        String upStationName = "잠실";
        String lineName = "2호선";
        String lineColor = "녹색";
        Long notExistDownStationId = 44L;
        Long distance = 3L;
        // given
        // 상행 종점역만 생성되어 있다.
        ExtractableResponse<Response> upStationCreatedResponse = CREATED_STATION(new StationRequest(upStationName));
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreatedResponse);

        // when
        // 지하철 노선 생성을 요청한다.
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(
                new LineRequest(lineName, lineColor, upStationId, notExistDownStationId, distance));

        // then
        // 지하철 노선 등록 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithAlreadyExistLine() {
        String lineName = "9호선";
        String lineColor = "금색";
        Long distance = 5L;

        // given
        // 지하철_노선_등록되어_있음
        // and 상행역 생성되어 있음
        // and 하행역 생성되어 있음
        ExtractableResponse<Response> upStationCreated = CREATED_STATION(new StationRequest("갱남역"));
        ExtractableResponse<Response> downStationCreated = CREATED_STATION(new StationRequest("서초역"));
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreated);
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationCreated);
        LineRequest lineRequest = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);
        LINE_ALREADY_CREATED(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행종점역과 하행종점역을 같은 지하철 역으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithSameUpDownStationTest() {
        String stationName = "몽촌토성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long distance = 10L;
        // given
        // 역이 생성되어 있다.
        ExtractableResponse<Response> stationCreateResponse = CREATED_STATION(new StationRequest(stationName));
        Long stationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(stationCreateResponse);

        // when
        // 상행종점역과 하행종점역을 같은 역으로 신규 지하철 노선을 생성한다.
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(
                new LineRequest(lineName, lineColor, stationId, stationId, distance));

        // then
        // 지하철 노성 생성 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("거리가 0인 지하철 노선을 생성한다.")
    @Test
    void createLineWithZeroDistance() {
        Long zeroDistance = 0L;
        String lineName = "비 내리는 호남선";
        String lineColor = "남행열차 색";
        // given
        // 상행종점역이 등록되어 있다.
        ExtractableResponse<Response> upStationResponse = CREATED_STATION(new StationRequest("갱남"));
        // and 하행종점역이 등록되어 있다.
        ExtractableResponse<Response> downStationResponse = CREATED_STATION(new StationRequest("서초"));

        // when
        // 거리가 0인 노선을 생성 요청
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationResponse);
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationResponse);
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(
                new LineRequest(lineName, lineColor, upStationId, downStationId, zeroDistance));

        // then
        // 노선 생성 실패함
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 생성 시 필수인 값이 빠진채로 신규 지하철 노선 생성")
    @Test
    void createLineWithoutEndStation() {
        String lineName = "퇴근선";
        String lineColor = "천국의 색";
        // when
        // 상행종점역이나 하행종점역이 빠진채로 노선 생성 요청
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(
                new LineRequest(lineName, lineColor, null, null, 12L)
        );

        // then
        // 노선 생성 실패함
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        String line1Name = "9호선";
        String line2Name = "2호선";
        String line1Color = "금색";
        String line2Color = "초록색";
        // given
        // 상행종점1 생성되어 있음
        ExtractableResponse<Response> upStation1Created = CREATED_STATION(new StationRequest("갱남역"));
        Long upStation1Id = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStation1Created);
        // 하행종점1 생성되어 있음
        ExtractableResponse<Response> downStation1Created = CREATED_STATION(new StationRequest("서초역"));
        Long downStation1Id = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStation1Created);
        // and 지하철_노선1_등록되어_있음
        ExtractableResponse<Response> line1CreatedResponse
                = LINE_ALREADY_CREATED(new LineRequest(line1Name, line1Color, upStation1Id, downStation1Id, 5L));
        // 상행종점2 생성되어 있음
        ExtractableResponse<Response> upStation2Created = CREATED_STATION(new StationRequest("잠실역"));
        Long upStation2Id = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStation2Created);
        // and 하행종점2 생성되어있음
        ExtractableResponse<Response> downStation2Created = CREATED_STATION(new StationRequest("몽촌토성역"));
        Long downStation2Id = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStation2Created);
        // and 지하철_노선2_등록되어_있음
        ExtractableResponse<Response> line2CreatedResponse
                = LINE_ALREADY_CREATED(new LineRequest(line2Name, line2Color, upStation2Id, downStation2Id, 10L));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = REQUEST_LINES();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        LINES_INCLUDED_IN_LIST(line1CreatedResponse, line2CreatedResponse, response);
    }

    @DisplayName("특정 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        String lineName = "9호선";
        String lineColor = "금색";
        // given
        // 상행종점역 등록되어 있음
        ExtractableResponse<Response> upStationCreated = CREATED_STATION(new StationRequest("갱남역"));
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreated);
        // and 하향종점역 등록되어 있음
        ExtractableResponse<Response> downStationCreated = CREATED_STATION(new StationRequest("서초역"));
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationCreated);
        // and 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LINE_ALREADY_CREATED(
                new LineRequest(lineName, lineColor, upStationId, downStationId, 10L)
        );
        Long createdLineId = EXTRACT_ID_FROM_RESPONSE_LOCATION(createdResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = REQUEST_ONE_SPECIFIC_LINE(createdLineId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        RESPONSE_INCLUDED_STATIONS(response, upStationId, downStationId);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineWhenNotExist() {
        Long notExistId = 0L;

        ExtractableResponse<Response> response = REQUEST_ONE_SPECIFIC_LINE(notExistId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        String lineName = "비 내리는 호남선";
        String lineColor = "남행열차색";
        // given
        // 상행종점역 등록되어 있음
        ExtractableResponse<Response> upStationCreated = CREATED_STATION(new StationRequest("갱남역"));
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreated);
        // and 하향종점역 등록되어 있음
        ExtractableResponse<Response> downStationCreated = CREATED_STATION(new StationRequest("서초역"));
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationCreated);
        // and 지하철_노선_등록되어_있음
        ExtractableResponse<Response> response = LINE_ALREADY_CREATED(
                new LineRequest(lineName, lineColor, upStationId, downStationId, 10L)
        );

        // when
        // 지하철_노선_수정_요청
        Long lineId = EXTRACT_ID_FROM_RESPONSE_LOCATION(response);
        LineRequest lineRequest = new LineRequest(lineName, lineColor, upStationId, downStationId, 12L);
        ExtractableResponse<Response> resultResponse = REQUEST_LINE_UPDATE(lineId, lineRequest);

        // then
        // 지하철_노선_수정됨
        assertThat(resultResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateWithNotExistLine() {
        Long notExistId = 4L;
        // given
        // 상행선역 등록되어 있음
        ExtractableResponse<Response> upStationResponse = CREATED_STATION(new StationRequest("갱남역"));
        // 하행선역 등록되어 있음
        ExtractableResponse<Response> downStationResponse = CREATED_STATION(new StationRequest("서초역"));
        // 등록된_지하철_노선_없음

        // when
        // 지하철_노선_수정_요청
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationResponse);
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationResponse);
        LineRequest lineRequest = new LineRequest(
                "notExist", "notExist", upStationId, downStationId, 10L);
        ExtractableResponse<Response> response = REQUEST_LINE_UPDATE(notExistId, lineRequest);

        // then
        // 지하철_노선_찾을수_없음
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        String lineName = "2020호선";
        String lineColor = "무지개색";
        // given
        // 상행종점역 등록되어 있음
        ExtractableResponse<Response> upStationCreated = CREATED_STATION(new StationRequest("갱남역"));
        Long upStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(upStationCreated);
        // and 하향종점역 등록되어 있음
        ExtractableResponse<Response> downStationCreated = CREATED_STATION(new StationRequest("서초역"));
        Long downStationId = EXTRACT_ID_FROM_RESPONSE_LOCATION(downStationCreated);
        // and 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LINE_ALREADY_CREATED(
                new LineRequest(lineName, lineColor, upStationId, downStationId, 10L)
        );
        Long createdId = EXTRACT_ID_FROM_RESPONSE_LOCATION(createdResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = REQUEST_LINE_DELETE(createdId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거 시도 시 실패")
    @Test
    void deleteLineFailTest() {
        Long notExistLineId = 4L;
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = REQUEST_LINE_DELETE(notExistLineId);

        // then
        // 지하철_노선_제거_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
