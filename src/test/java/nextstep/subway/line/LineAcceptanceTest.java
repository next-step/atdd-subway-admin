package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest params;
    ExtractableResponse<Response> stationResponse1;
    ExtractableResponse<Response> stationResponse2;

    @BeforeEach
    void setUpLine() {

        params = new LineRequest("신분당선", "bg-red-660", 1L, 2L, 10);
        stationResponse1 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        stationResponse2 = 지하철_역_등록되어_있음(new StationRequest("광교역"));
    }


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성됨(response);
        지하철_노선에_역_목록_포함됨(Arrays.asList(stationResponse1, stationResponse2), response);
    }

    @DisplayName("구간 정보가 없는 지하철 노선을 생성한다.")
    @Test
    void createLineWithoutStation() {
        // given
        LineRequest params = new LineRequest("신분당선", "bg-red-660");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음(params);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "bg-green-660", 1L, 2L, 6));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(Arrays.asList(createResponse1, createResponse2), response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, params);

        // then
        지하철_노선_수정됨(response);

    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createResponse);

        // then
        지하철_노선_삭제됨(response);
    }

    private void 지하철_노선에_역_목록_포함됨(List<ExtractableResponse<Response>> stationResponseList, ExtractableResponse<Response> response) {
        List<Long> resultStationIds = response.jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = stationResponseList.stream()
                .map(this::getIdFromCreateResponse)
                .collect(Collectors.toList());

        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        지하철_노선_응답됨(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        지하철_노선_응답됨(response);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        지하철_노선_응답됨(response);
        assertThat(response.body().jsonPath().getList(".", LineResponse.class)).hasSize(2);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(List<ExtractableResponse<Response>> createResponseList, ExtractableResponse<Response> response) {
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedLineIds = createResponseList.stream()
                .map(this::getIdFromCreateResponse)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private long getIdFromCreateResponse(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }    
}
