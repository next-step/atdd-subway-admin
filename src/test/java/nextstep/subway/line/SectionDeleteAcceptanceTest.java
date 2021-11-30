package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationApiRequests;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 삭제 기능")
public class SectionDeleteAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 잠실역;
    private StationResponse 강변역;
    private Long 서울2호선;
    private int 강남잠실구간길이 = 10;
    private int 잠실강변구간길이 = 7;

    @BeforeEach
    void setUpSectionAcceptanceTest() {
        super.setUp();

        강남역 = StationApiRequests.지하철_역_생성됨("강남역");
        잠실역 = StationApiRequests.지하철_역_생성됨("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", 강남역.getId(), 잠실역.getId(), 강남잠실구간길이);
        서울2호선 = LineHelper.지하철_노선_등록되어_있음(lineRequest);
        강변역 = StationApiRequests.지하철_역_생성됨("강변역");
        SectionApiRequests.지하철_구간_등록_요청(서울2호선, new SectionRequest(잠실역.getId(), 강변역.getId(), 잠실강변구간길이));
    }

    @DisplayName("두개의 연속된 구간에서 중간역을 제거하면 남은 두 역을 새로운 구간으로 한다.")
    @Test
    void deleteInnerStation() {
        // when
        // 구간 삭제 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, 잠실역.getId());

        // then
        // 노선에 구간 삭제됨.
        지하철_노선_구간_삭제됨(response);
        지하철_노선에_등록된_역_확인(서울2호선,"강남역","강변역");
    }

    @DisplayName("두개의 연속된 구간의 상행 종점역을 제거하면 하나의 구간만 남는다.")
    @Test
    void deleteUpStation() {
        // when
        // 구간 삭제 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, 강남역.getId());

        // then
        // 노선에 구간 삭제됨.
        지하철_노선_구간_삭제됨(response);
        지하철_노선에_등록된_역_확인(서울2호선,"잠실역","강변역");
    }

    @DisplayName("두개의 연속된 구간의 하행 종점역을 제거하면 하나의 구간만 남는다.")
    @Test
    void deleteDownStation() {
        // when
        // 구간 삭제 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, 강변역.getId());

        // then
        // 노선 구간 삭제됨.
        지하철_노선_구간_삭제됨(response);
        지하철_노선에_등록된_역_확인(서울2호선,"강남역","잠실역");
    }

    @DisplayName("노선에 없는 역을 제거한다")
    @Test
    void deleteNotExistsStation() {
        // given
        // 노선 구간에 없는 역 생성
        StationResponse 홍대입구역 = StationApiRequests.지하철_역_생성됨("홍대입구역");

        // when
        // 구간 삭제 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, 홍대입구역.getId());

        // then
        // 노선 삭제 실패함
        지하철_노선_구간_삭제_실패함(response, "구간에 없는 역을 제거할 수 없습니다.");
    }

    @DisplayName("구간이 하나인 노선에서 역을 제거한다")
    @Test
    void deleteStationInOnlyOneSection() {
        // given
        // 하나의 구간만 존재한다
        지하철_노선_구간_삭제함(서울2호선, 강변역.getId());

        // when
        // 구간 삭제 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, 강남역.getId());

        // then
        // 노선 삭제 실패함
        지하철_노선_구간_삭제_실패함(response, "하나의 구간만 있을시 삭제할 수 없습니다.");
    }

    private void 지하철_노선_구간_삭제함(Long 서울2호선, Long stationId) {
        ExtractableResponse<Response> response = SectionApiRequests.지하철_구간_삭제_요청(서울2호선, stationId);
        지하철_노선_구간_삭제됨(response);
    }

    private void 지하철_노선_구간_생성_실패함(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.jsonPath().getObject("message", String.class)).isEqualTo(message);
    }

    private void 지하철_노선_구간_삭제_실패함(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.jsonPath().getObject("message", String.class)).isEqualTo(message);
    }

    private void 지하철_노선_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_등록된_역_확인(Long lineId, String... names) {
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);
        LineResponse result = response.as(LineResponse.class);
        List<String> savedStationNames = result.getStations()
                .stream()
                .map(stationResponse -> stationResponse.getName())
                .collect(Collectors.toList());
        assertThat(savedStationNames).containsExactly(names);
    }

    private void 지하철_노선_구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
