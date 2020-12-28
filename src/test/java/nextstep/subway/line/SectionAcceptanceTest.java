package nextstep.subway.line;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.StationAcceptanceTestRequest;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long lineUpStationId;
    private Long lineDownStationId;
    private Long lineNewStationId;
    private String lineUri;
    private static int INIT_LINE_DISTANCE = 10;


    @BeforeEach
    public void setUp() {
        super.setUp();
        // 지하철역_등록되어_있음
        // 지하철_노선_등록되어_있음
        lineUpStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("문래역");
        lineDownStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("잠실역");
        lineNewStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("사당역");
        lineUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", lineUpStationId, lineDownStationId, 10);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE1) A-NEW-B : A-B 중간에 A-NEW등록")
    @Test
    void addSectionInMiddleBasedPreStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, INIT_LINE_DISTANCE - 1);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineNewStationId, lineDownStationId);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_등록한_구간_포함됨(response, expectedStations);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE2) A-NEW-B : A-B 중간에 NEW-B등록")
    @Test
    void addSectionInMiddle() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineNewStationId, lineDownStationId, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineNewStationId, lineDownStationId);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_등록한_구간_포함됨(response, expectedStations);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE3) NEW-A-B : A-B 앞에 NEW-A등록")
    @Test
    void addSectionAtFirst() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineNewStationId, lineUpStationId, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> expectedStations = Arrays.asList(lineNewStationId, lineUpStationId, lineDownStationId);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_등록한_구간_포함됨(response, expectedStations);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE4) A-B-NEW: A-B 뒤에 B-C등록")
    @Test
    void addSectionAtLastBasedPreStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineDownStationId, lineNewStationId, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineDownStationId, lineNewStationId);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_등록한_구간_포함됨(response, expectedStations);
    }

    @DisplayName("노선에 구간의 길이와 같이 등록하면 등록할 수 없다")
    @Test
    void addSectionSameDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, INIT_LINE_DISTANCE);

        // then
        // 지하철_노선에_지하철역_등록됨
        SectionAcceptanceTestResponse.지하철_노선에_지하철역이_등록되지않음(response);
    }

    @DisplayName("노선에 구간의 길이보다 긴 거리로 등록하면 등록할 수 없다")
    @Test
    void addSectionGreaterDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineDownStationId, lineNewStationId, INIT_LINE_DISTANCE + 1);

        // then
        // 지하철_노선에_지하철역_등록됨
        SectionAcceptanceTestResponse.지하철_노선에_지하철역이_등록되지않음(response);
    }

    @DisplayName("노선에 이미 등록된 역들로 구간을 등록할 수 없다")
    @Test
    void addAlreadyExistStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineDownStationId, lineUpStationId, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        SectionAcceptanceTestResponse.지하철_노선에_지하철역이_등록되지않음(response);
    }

    @DisplayName("노선에 등록된 역이 하나도 없는 구간은 등록할 수 없다")
    @Test
    void addNoExistStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, 99L, 100L, 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineDownStationId, lineNewStationId);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역이_등록되지않음(response);
    }
}
