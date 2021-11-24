package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineAcceptanceTestRequest;
import nextstep.subway.line.dto.LineAcceptanceTestResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.line.dto.LineAcceptanceTestRequest.*;
import static nextstep.subway.line.dto.LineAcceptanceTestResponse.isNoContent;
import static nextstep.subway.line.dto.LineAcceptanceTestResponse.isStatusOk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    protected void createLine() {
        //given
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.createLine("1호선", "blue", "청량리역", "영등포역", 10);

        // then
        // 지하철_노선_생성됨
        LineAcceptanceTestResponse.checkCreate(response);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    protected void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceTestRequest.createLine("1호선", "blue", 1L, 2L, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.createLine("1호선", "blue", 1L, 2L, 10);
        // then
        // 지하철_노선_생성_실패됨
        LineAcceptanceTestResponse.failCreatedLine(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineAcceptanceTestRequest.createLine("1호선", "blue", "청량리역", "영등포역", 10);
        // when
        // 지하철_노선_조회_요청
        String savedLineId = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> selectResponse = selectOneLine(savedLineId);
        // then
        // 지하철_노선_응답됨
        isStatusOk(selectResponse);
        validLine(createResponse, selectResponse);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestRequest.createLine("1호선", "blue", "청량리역", "영등포역", 10);
        ExtractableResponse<Response> createResponse2 = LineAcceptanceTestRequest.createLine("2호선", "green", "당산역", "한양대역", 10);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = selectAllLines();
        // then
        isStatusOk(response);
        validLines(createResponse1, createResponse2, response);
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestRequest.createLine("1호선", "blue", "청량리역", "영등포역", 10);
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestModifyLine(createResponse1, "1", "2호선", "green", "홍대입구역", "이대역", 20);
        // then
        // 지하철_노선_수정됨
        isStatusOk(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceTestRequest.createLine("1호선", "blue", "청량리역", "영등포역", 10);
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = removeLine("1");

        // then
        // 지하철_노선_삭제됨
        isNoContent(response);

    }


    private void validLine(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> selectResponse) {
        LineResponse createLineResponse = createResponse.jsonPath()
                .getObject(".", LineResponse.class);
        LineResponse selectLineResponse = selectResponse.jsonPath()
                .getObject(".", LineResponse.class);

        compareValues(createLineResponse, selectLineResponse);
    }

    private void compareValues(LineResponse createLineResponse, LineResponse selectLineResponse) {
        List<StationResponse> createStations = createLineResponse.getStations();
        List<StationResponse> selectStations = selectLineResponse.getStations();

        assertAll(
                () -> assertThat(createLineResponse.getId()).isEqualTo(selectLineResponse.getId()),
                () -> assertThat(createLineResponse.getName()).isEqualTo(selectLineResponse.getName()),
                () -> assertThat(createLineResponse.getColor()).isEqualTo(selectLineResponse.getColor()),
                () -> checkDupilicateStationName(createStations, selectStations)
        );
    }

    private void checkDupilicateStationName(List<StationResponse> createStations, List<StationResponse> selectStations) {
        for (int i = 0; i < createStations.size(); i++) {
            assertThat(createStations.get(i).getName()).isEqualTo(selectStations.get(i).getName());
        }
    }

    private void validLines(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2,
                            ExtractableResponse<Response> response) {
        LineResponse createLineResponse1 = createResponse1.jsonPath()
                .getObject(".", LineResponse.class);
        LineResponse createLineResponse2 = createResponse2.jsonPath()
                .getObject(".", LineResponse.class);

        List<LineResponse> selectLineResponses = response.jsonPath()
                .getList(".", LineResponse.class);

        compareValues(createLineResponse1, selectLineResponses.get(0));
        compareValues(createLineResponse2, selectLineResponses.get(1));
    }
}
