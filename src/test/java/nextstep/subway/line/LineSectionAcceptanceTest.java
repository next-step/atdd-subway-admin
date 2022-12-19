package nextstep.subway.line;

import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertCannotRemoveSectionMessage;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertConnectedStationNotPresentMessage;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertDuplicatedSectionMessage;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertInternalServerErrorStatus;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertInvalidSectionDistanceMessage;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertLineStationOrder;
import static nextstep.subway.line.LineSectionAcceptanceTestHelper.assertOkStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구관 관련 기능 인수 테스트")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 광교역;
    private StationResponse 정자역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTestHelper.createStation("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTestHelper.createStation("양재역").as(StationResponse.class);
        광교역 = StationAcceptanceTestHelper.createStation("광교역").as(StationResponse.class);
        정자역 = StationAcceptanceTestHelper.createStation("정자역").as(StationResponse.class);

        신분당선 = LineAcceptanceTestHelper.createLine(new LineRequestParamsBuilder()
            .withName("신분당선")
            .withColor("bg-red-600")
            .withUpStation(강남역.getId())
            .withDownStation(광교역.getId())
            .withDistance(10)
            .build()).as(LineResponse.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addSection_duplicatedSection() {
        ExtractableResponse<Response> response = createLineSection(신분당선, 강남역, 광교역, 3);
        assertInternalServerErrorStatus(response);
        assertDuplicatedSectionMessage(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 기존 노선에 포함되어있지 않으면 추가할 수 없다")
    @Test
    void addSection_notFoundConnectedStation() {
        ExtractableResponse<Response> response = createLineSection(신분당선, 정자역, 양재역, 3);
        assertInternalServerErrorStatus(response);
        assertConnectedStationNotPresentMessage(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다")
    @Test
    void addSection_longerThanCurrentDistance() {
        ExtractableResponse<Response> response = createLineSection(신분당선, 강남역, 양재역, 10);
        assertInternalServerErrorStatus(response);
        assertInvalidSectionDistanceMessage(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다")
    @Test
    void addSection_nextToHeadStation() {
        StationResponse 새종점 = StationAcceptanceTestHelper.createStation("새종점").as(StationResponse.class);
        createLineSection(신분당선, 새종점, 강남역, 5);

        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(새종점, 강남역, 광교역));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 수 있다")
    @Test
    void addSection_beforeTailStation() {
        StationResponse 새종점 = StationAcceptanceTestHelper.createStation("새종점").as(StationResponse.class);
        createLineSection(신분당선, 광교역, 새종점, 5);

        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(강남역, 광교역, 새종점));
    }

    @DisplayName("이미 존재 하는 구간 사이에 새로운 구간을 등록할 수 있다")
    @Test
    void addSection_betweenSection() {
        createLineSection(신분당선, 강남역, 양재역, 2);
        createLineSection(신분당선, 정자역, 강남역, 5);

        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(정자역, 강남역, 양재역, 광교역));
    }

    @DisplayName("오직 하나의 구간만 존재 할 경우, 해당 구간은 제거 할 수 없다")
    @Test
    void removeLineStation_onlyOneSectionRemaining() {
        ExtractableResponse<Response> response = removeLineSection(신분당선, 강남역);
        assertInternalServerErrorStatus(response);
        assertCannotRemoveSectionMessage(response);
    }

    @DisplayName("노선에 등록 되어있지 않은 역은 제거할 수 없다")
    @Test
    void removeLineStation_notExistsLine() {
        // given
        createLineSection(신분당선, 강남역, 양재역, 2);
        // when
        ExtractableResponse<Response> response = removeLineSection(신분당선, 정자역);
        // then
        assertInternalServerErrorStatus(response);
        assertCannotRemoveSectionMessage(response);
    }

    @DisplayName("노선 중 첫 역 구간을 제거할경우, 해당 구간만 성공적으로 제거 할 수 있어야 한다")
    @Test
    void removeLineStation_head() {
        // given
        createLineSection(신분당선, 강남역, 양재역, 2);
        // when
        removeLineSection(신분당선, 강남역);
        // then
        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(양재역, 광교역));
    }

    @DisplayName("노선 중 중간 역 구간을 제거할경우, 해당 구간만 성공적으로 제거 할 수 있어야 한다")
    @Test
    void removeLineStation_middle() {
        // given
        createLineSection(신분당선, 강남역, 양재역, 2);
        // when
        removeLineSection(신분당선, 양재역);
        // then
        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(강남역, 광교역));
    }

    @DisplayName("노선 중 마지막 역 구간을 제거할경우, 해당 구간만 성공적으로 제거 할 수 있어야 한다")
    @Test
    void removeLineStation_tail() {
        // given
        createLineSection(신분당선, 강남역, 양재역, 2);
        // when
        removeLineSection(신분당선, 광교역);
        // then
        ExtractableResponse<Response> response = LineAcceptanceTestHelper.getLineResponse(신분당선);
        assertOkStatus(response);
        assertLineStationOrder(response, Arrays.asList(강남역, 양재역));
    }

    private static ExtractableResponse<Response> createLineSection(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        return LineSectionAcceptanceTestHelper.createLineSection(line.getId(), createSectionRequestParams(upStation, downStation, distance));
    }

    private static Map<String, String> createSectionRequestParams(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> sectionRequestParams = new HashMap<>();
        sectionRequestParams.put("upStationId", upStation.getId().toString());
        sectionRequestParams.put("downStationId", downStation.getId().toString());
        sectionRequestParams.put("distance", Integer.toString(distance));
        return sectionRequestParams;
    }

    private ExtractableResponse<Response> removeLineSection(LineResponse line, StationResponse station) {
        return LineSectionAcceptanceTestHelper.deleteLineSection(line.getId(), station.getId());
    }
}
