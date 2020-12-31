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

    @DisplayName("노선에 구간을 등록한다. (CASE1) A-NEW-B : A-B 중간에 A-NEW 등록")
    @Test
    void addSectionInMiddleBasedPreStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        int newSectionDistance = INIT_LINE_DISTANCE - 1;
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, newSectionDistance);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineNewStationId, lineDownStationId);
        List<Integer> expectedDistances = Arrays.asList(0, newSectionDistance, INIT_LINE_DISTANCE - newSectionDistance);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE2) A-NEW-B : A-B 중간에 NEW-B 등록")
    @Test
    void addSectionInMiddle() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        int newSectionDistance = 4;
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineNewStationId, lineDownStationId, newSectionDistance);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineNewStationId, lineDownStationId);
        List<Integer> expectedDistances = Arrays.asList(0, INIT_LINE_DISTANCE - newSectionDistance, newSectionDistance);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE3) NEW-A-B : A-B 앞에 NEW-A 등록")
    @Test
    void addSectionAtFirst() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        int newSectionDistance = 4;
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineNewStationId, lineUpStationId, newSectionDistance);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨
        List<Long> expectedStations = Arrays.asList(lineNewStationId, lineUpStationId, lineDownStationId);
        List<Integer> expectedDistances = Arrays.asList(0, newSectionDistance, INIT_LINE_DISTANCE);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간을 등록한다. (CASE4) A-B-NEW: A-B 뒤에 B-NEW 등록")
    @Test
    void addSectionAtLastBasedPreStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        int newSectionDistance = 4;
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineDownStationId, lineNewStationId, newSectionDistance);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineDownStationId, lineNewStationId);
        List<Integer> expectedDistances = Arrays.asList(0, INIT_LINE_DISTANCE, newSectionDistance);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_등록됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간의 길이와 같이 등록하면 등록할 수 없다")
    @Test
    void addSectionSameDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, INIT_LINE_DISTANCE);

        // then
        // 지하철_노선에_유효하지않은_구간정보는_등록되지않음
        SectionAcceptanceTestResponse.지하철_노선에_유효하지않은_구간정보는_등록되지않음(response);
    }

    @DisplayName("노선에 구간의 길이보다 긴 거리로 등록하면 등록할 수 없다")
    @Test
    void addSectionGreaterDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineNewStationId, lineDownStationId, INIT_LINE_DISTANCE + 1);

        // then
        // 지하철_노선에_유효하지않은_구간정보는_등록되지않음
        SectionAcceptanceTestResponse.지하철_노선에_유효하지않은_구간정보는_등록되지않음(response);
    }

    @DisplayName("노선에 이미 등록된 역들로 구간을 등록할 수 없다")
    @Test
    void addAlreadyExistStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, lineDownStationId, lineUpStationId, 4);

        // then
        // 지하철_노선에_유효하지않은_구간정보는_등록되지않음
        SectionAcceptanceTestResponse.지하철_노선에_유효하지않은_구간정보는_등록되지않음(response);
    }

    @DisplayName("노선에 등록된 역이 하나도 없는 구간은 등록할 수 없다")
    @Test
    void addNoExistStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Long notInLineStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("신사역");
        Long notInLineStationId2 = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID("용산역");
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest
            .지하철_노선에_지하철역_등록_요청(lineUri, notInLineStationId, notInLineStationId2, 4);

        // then
        // 지하철_노선에_유효하지않은_구간정보는_등록되지않음
        SectionAcceptanceTestResponse.지하철_노선에_유효하지않은_구간정보는_등록되지않음(response);
    }

    @DisplayName("노선에 구간을 제거한다. (CASE1) A-NEW-B : NEW 제거")
    @Test
    void deleteMiddleSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청 [A-(4)-NEW-(6)-B]
        SectionAcceptanceTestRequest.지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, 4);

        // 지하철_노선에_지하철역_제거_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest.지하철_노선에_지하철역_제거_요청(lineUri, lineNewStationId);

        // then
        // 지하철_노선에_지하철역_제거됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨 [A-(10)-B]
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineDownStationId);
        List<Integer> expectedDistances = Arrays.asList(0, 10);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_제거됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간을 제거한다. (CASE1) A-NEW-B : A 제거")
    @Test
    void deleteStartSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청 [A-(4)-NEW-(6)-B]
        SectionAcceptanceTestRequest.지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, 4);

        // when
        // 지하철_노선에_지하철역_제거_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest.지하철_노선에_지하철역_제거_요청(lineUri, lineUpStationId);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨 [NEW-(6)-B]
        List<Long> expectedStations = Arrays.asList(lineNewStationId, lineDownStationId);
        List<Integer> expectedDistances = Arrays.asList(0, 6);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_제거됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("노선에 구간을 제거한다. (CASE1) A-NEW-B : B 제거")
    @Test
    void deleteEndSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청 [A-(4)-NEW-(6)-B]
        SectionAcceptanceTestRequest.지하철_노선에_지하철역_등록_요청(lineUri, lineUpStationId, lineNewStationId, 4);

        // when
        // 지하철_노선에_지하철역_제거_요청
        ExtractableResponse<Response> response = SectionAcceptanceTestRequest.지하철_노선에_지하철역_제거_요청(lineUri, lineDownStationId);

        // then
        // 지하철_노선에_지하철역_등록됨
        // 지하철_노선에_구간_포함됨
        // 지하철_노선_구간_거리_계산됨 [A-(4)-NEW]
        List<Long> expectedStations = Arrays.asList(lineUpStationId, lineNewStationId);
        List<Integer> expectedDistances = Arrays.asList(0, 4);
        SectionAcceptanceTestResponse.지하철_노선에_지하철역_제거됨(response);
        SectionAcceptanceTestResponse.지하철_노선에_구간_포함됨(response, expectedStations);
        SectionAcceptanceTestResponse.지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }
}
