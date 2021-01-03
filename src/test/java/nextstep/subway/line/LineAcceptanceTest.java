package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTestRequest.*;
import static nextstep.subway.station.StationAcceptanceTestRequest.지하철역_생성_요청;
import static nextstep.subway.utils.HttpTestStatusCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String _1호선_이름 = "1호선";
    private static final String _1호선_색상 = "BLUE";
    private static final String _2호선_이름 = "2호선";
    private static final String _2호선_색상 = "GREEN";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        long _2호선_상행역_Id = 지하철역_생성_요청("홍대역").getId();
        long _2호선_하행역_Id = 지하철역_생성_요청("잠실역").getId();
        // When
        LineResponse _1호선 = 지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        LineResponse _2호선 = 지하철_노선_생성_요청(_2호선_이름, _2호선_색상, _2호선_상행역_Id, _2호선_하행역_Id, 1000L);
        // Then
        지하철_노선_정보_확인(_1호선, _1호선_이름, _1호선_색상, Arrays.asList(_1호선_상행역_Id, _1호선_하행역_Id));
        지하철_노선_정보_확인(_2호선, _2호선_이름, _2호선_색상, Arrays.asList(_2호선_상행역_Id, _2호선_하행역_Id));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createDuplicatedLine() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // When
        ExtractableResponse<Response> 재생성된_1호선_응답 = 지하철_노선_재생성후_응답(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // Then
        지하철_노선_생성_실패됨(재생성된_1호선_응답);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        long _2호선_상행역_Id = 지하철역_생성_요청("홍대역").getId();
        long _2호선_하행역_Id = 지하철역_생성_요청("잠실역").getId();
        LineResponse _1호선 = 지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        LineResponse _2호선 = 지하철_노선_생성_요청(_2호선_이름, _2호선_색상, _2호선_상행역_Id, _2호선_하행역_Id, 1000L);
        // When
        List<LineResponse> 지하철_노선_목록 = 지하철_노선_목록_조회_요청();
        // Then
        지하철_노선_목록_포함됨(지하철_노선_목록, Arrays.asList(_1호선, _2호선));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        LineResponse _1호선 = 지하철_노선_생성_요청(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // When
        LineResponse 응답된_1호선 = 지하철_노선_조회(_1호선.getId());
        // Then
        지하철_노선_응답됨(응답된_1호선, _1호선);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        ExtractableResponse<Response> _1호선 = 지하철_노선_생성_요청후_응답(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // When
        ExtractableResponse<Response> 수정된_1호선_응답 = 지하철_노선_수정_요청후_응답(_1호선, _1호선_이름, _1호선_색상);
        // Then
        지하철_노선_수정됨(수정된_1호선_응답, _1호선_이름, _1호선_색상);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // Given
        long _1호선_상행역_Id = 지하철역_생성_요청("청량리역").getId();
        long _1호선_하행역_Id = 지하철역_생성_요청("천호역").getId();
        ExtractableResponse<Response> _1호선 = 지하철_노선_생성_요청후_응답(_1호선_이름, _1호선_색상, _1호선_상행역_Id, _1호선_하행역_Id, 500L);
        // When
        ExtractableResponse<Response> 삭제된_1호선_응답 = 지하철_노선_제거_요청(_1호선);
        // Then
        지하철_노선_삭제됨(삭제된_1호선_응답);
    }

    private void 지하철_노선_정보_확인(LineResponse line, String name, String color, List<Long> upAndDownStationId) {
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(name),
                () -> assertThat(line.getColor()).isEqualTo(color),
                () -> assertThat(line.getStations()).hasSize(upAndDownStationId.size()),
                () -> assertThat(line.getStations())
                        .extracting(StationResponse::getId)
                        .containsExactlyElementsOf(upAndDownStationId)
                        .containsSequence(upAndDownStationId)
        );
    }

    private void 지하철_노선_목록_포함됨(List<LineResponse> actual, List<LineResponse> expected) {
        assertAll(
                () -> assertThat(actual).hasSameSizeAs(expected),
                () -> assertEquals(actual, expected)
        );
    }

    private void 지하철_노선_응답됨(LineResponse actual, LineResponse expected) {
        assertEquals(actual, expected);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, String name, String color) {
        요청_완료(response);
        assertAll(
                () -> assertEquals(response.jsonPath().getString("name"), name),
                () -> assertEquals(response.jsonPath().getString("color"), color)
        );
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        서버_내부_에러(response);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        컨텐츠_없음(response);
    }
}
