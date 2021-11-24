package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationApiRequests;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 강남역 = StationApiRequests.지하철_역_생성됨("강남역");
        StationResponse 잠실역 = StationApiRequests.지하철_역_생성됨("잠실역");
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 잠실역.getId(), 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = 지하철_노선_생성_요청_생성("강남역","잠실역","신분당선", "bg-red-600",10);
        LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨_지하철_역_없음(response);
    }

    @DisplayName("존재하지 않는 지하철_역을 노선에 등록한다")
    @Test
    void createLine3() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse 강남역 = StationApiRequests.지하철_역_생성됨("강남역");
        Long 등록되지_않은_역_ID = 5L;
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 등록되지_않은_역_ID, 10);
        LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨_지하철_역_없음(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest1 = 지하철_노선_생성_요청_생성("강남역", "잠실역", "2호선", "bg-green-200", 10);
        LineRequest lineRequest2 = 지하철_노선_생성_요청_생성("사당역", "동작역", "4호선", "bg-blue-400", 20);
        Long id1 = 지하철_노선_등록되어_있음(lineRequest1);
        Long id2 = 지하철_노선_등록되어_있음(lineRequest2);


        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(id1, id2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest1 = 지하철_노선_생성_요청_생성("강남역","잠실역","2호선","bg-green-200",10);
        Long lineId = 지하철_노선_등록되어_있음(lineRequest1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답(response, lineId, lineRequest1);
        지하철_노선에_등록된_역_확인(response,"강남역","잠실역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest1 = 지하철_노선_생성_요청_생성("강남역","잠실역","2호선","bg-green-200",10);
        Long lineId = 지하철_노선_등록되어_있음(lineRequest1);
        LineRequest updateRequest = new LineRequest("구분당선", "bg-blue-600");

        // when
        // 지하철_노선_수정_요청
        LineApiRequests.지하철_노선_수정_요청(lineId, updateRequest);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(lineId, updateRequest);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest1 = 지하철_노선_생성_요청_생성("강남역","잠실역","2호선","bg-green-200",10);
        Long lineId = 지하철_노선_등록되어_있음(lineRequest1);

        // when
        // 지하철_노선_제거_요청
        LineApiRequests.지하철_노선_제거_요청(lineId);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(lineId);
    }



    private void 지하철_노선_삭제됨(Long lineId) {
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_응답(ExtractableResponse<Response> response, Long lineId, LineRequest lineRequest) {
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(result.getId()).isEqualTo(lineId);
        assertThat(result.getName()).isEqualTo(lineRequest.getName());
        assertThat(result.getColor()).isEqualTo(lineRequest.getColor());
    }

    private LineRequest 지하철_노선_생성_요청_생성(String station1, String station2, String lineName, String color, int distance) {
        StationResponse stationResponse1 = StationApiRequests.지하철_역_생성됨(station1);
        StationResponse stationResponse2 = StationApiRequests.지하철_역_생성됨(station2);
        return new LineRequest(lineName, color, stationResponse1.getId(), stationResponse2.getId(), distance);
    }

    private Long 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> createResponse = LineApiRequests.지하철_노선_생성_요청(lineRequest);
        return Long.valueOf(createResponse.header("Location").split("/")[2]);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<Long> givenLines) {
        List<Long> resultList = response.jsonPath().getList("id", Long.class);
        assertThat(resultList).containsAll(givenLines);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨_지하철_역_없음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선_수정됨(Long lineId, LineRequest updateRequest) {
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(result.getId()).isEqualTo(lineId);
        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getColor()).isEqualTo(updateRequest.getColor());
    }

    private void 지하철_노선에_등록된_역_확인(ExtractableResponse<Response> response, String station1, String station2) {
        LineResponse lineResponse = response.as(LineResponse.class);
        List<String> names =
                lineResponse.getStations()
                        .stream()
                        .map(Station::getName)
                        .collect(Collectors.toList());
        assertThat(names).containsExactly("강남역","잠실역");
    }


}
