package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.LineAcceptanceTestUtil.지하철_노선_등록되어_있음_toId;
import static nextstep.subway.utils.LineStationAcceptanceTestUtil.*;
import static nextstep.subway.utils.StationAcceptanceTestUtil.지하철됨_역_생성_됨_toId;

@DisplayName("지하철 노선에 역 제거 관련 기능")
public class LineStationRemoveAcceptanceTest extends AcceptanceTest {

    private Long 노선ID;
    private Long 잠실역ID;
    private Long 몽촌토성역ID;
    private Long 강동구청역ID;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        잠실역ID = 지하철됨_역_생성_됨_toId("잠실역");
        몽촌토성역ID = 지하철됨_역_생성_됨_toId("몽촌토성역");
        강동구청역ID = 지하철됨_역_생성_됨_toId("강동구청역");

        노선ID = 지하철_노선_등록되어_있음_toId("2호선", "RED", 잠실역ID, 몽촌토성역ID, BASE_DISTANCE);
    }

    @DisplayName("상행종점,하행종점이 아닌 중간 역 제거")
    @Test
    void 중간_구간_제거() {
        // given
        지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 강동구청역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 구간제거응답 = 지하철_노선구간_제거_됨(노선ID, 강동구청역ID);

        // then
        지하철_구간_요청_응답_검증(구간제거응답, HttpStatus.NO_CONTENT);
    }


    @DisplayName("하행종점 역 제거")
    @Test
    void 하행종점_제거() {
        // given
        // 3개의 역을 생성됨
        지하철_노선구간_하행_종점_역_추가_되어_있음(노선ID, 강동구청역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 구간제거응답 = 지하철_노선구간_제거_됨(노선ID, 강동구청역ID);

        // then
        지하철_구간_요청_응답_검증(구간제거응답, HttpStatus.NO_CONTENT);
    }


    @DisplayName("상행종점 역 제거")
    @Test
    void 상행종점_제거() {
        // given
        지하철_노선구간_상행_종점_역_추가_되어_있음(노선ID, 강동구청역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 구간제거응답 = 지하철_노선구간_제거_됨(노선ID, 강동구청역ID);

        // then
        지하철_구간_요청_응답_검증(구간제거응답, HttpStatus.NO_CONTENT);
    }

    @DisplayName("구간 하나일때 제거 실패")
    @Test
    void 구간_하나_일때_실패() {
        // given
        // when
        ExtractableResponse<Response> 구간제거응답 = 지하철_노선구간_제거_됨(노선ID, 잠실역ID);

        // then
        지하철_구간_요청_응답_검증(구간제거응답, HttpStatus.BAD_REQUEST);
    }


    private ExtractableResponse<Response> 지하철_노선구간_사이에_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, stationId, 몽촌토성역ID, distance);
    }

    private ExtractableResponse<Response> 지하철_노선구간_상행_종점_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, stationId, 잠실역ID, distance);
    }

    private ExtractableResponse<Response> 지하철_노선구간_하행_종점_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, 몽촌토성역ID, stationId, distance);
    }
}
