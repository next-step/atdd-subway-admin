package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineSectionCreateRequest;
import nextstep.subway.station.StationAcceptanceTestRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private StationResponse gangNamStation;
    private StationResponse gwangKyoStation;
    private String lineUri;
    private String lindId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        gangNamStation = StationAcceptanceTestRequest.createStation("강남역").as(StationResponse.class);
        gwangKyoStation = StationAcceptanceTestRequest.createStation("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600"
                , gangNamStation.getId(), gwangKyoStation.getId(), 10);

        lineUri = LineAcceptanceTestRequest.createLineRequest(lineRequest).header("Location");
        lindId = lineUri.split("/")[2];
    }

    @Test
    @DisplayName("상행 종점에 section추가")
    public void insertUpStationSectionTest() {
        StationResponse shinNonHyunStation = StationAcceptanceTestRequest.createStation("신논현역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(shinNonHyunStation.getId(), gangNamStation.getId(), 3);

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        LineSectionAcceptanceTestResponse.isStatusOk(response);
        checkValidCompareStations(response);
    }

    @Test
    @DisplayName("하행 종점에 section추가")
    public void insertDownStationSectionTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gwangKyoStation.getId(), suwonStation.getId(), 3);

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());
        LineSectionAcceptanceTestResponse.isStatusOk(response);
        checkValidCompareStations(response);
    }

    @Test
    @DisplayName("중간 왼쪽에 new section추가")
    public void insertMiddleLeftStationSectionTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gwangKyoStation.getId(), suwonStation.getId(), 3);

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());
        LineSectionAcceptanceTestResponse.isStatusOk(response);
        checkValidCompareStations(response);
    }

    @Test
    @DisplayName("중간 오른쪽에 new section추가")
    public void insertMiddleRightStationSectionTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), suwonStation.getId(), 3);

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        LineSectionAcceptanceTestResponse.isStatusOk(response);
        checkValidCompareStations(response);
    }

    @Test
    @DisplayName("상행 종점 제거")
    public void removeUpEndStationTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), suwonStation.getId(), 3);

        LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.removeStation(lineUri, gangNamStation.getId());

        LineSectionAcceptanceTestResponse.isStatusOk(response);
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        checkValidCompareSearchStations(searchLineResponse, 2,"수원역","광교역");
    }

    @Test
    @DisplayName("하행 종점 제거")
    public void removeDownEndStationTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), suwonStation.getId(), 3);

        LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.removeStation(lineUri, gwangKyoStation.getId());

        LineSectionAcceptanceTestResponse.isStatusOk(response);
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        checkValidCompareSearchStations(searchLineResponse, 2,"강남역","수원역");
    }

    @Test
    @DisplayName("중간 역 제거")
    public void removeMiddleStationTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), suwonStation.getId(), 3);

        LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.removeStation(lineUri, suwonStation.getId());

        LineSectionAcceptanceTestResponse.isStatusOk(response);
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        checkValidCompareSearchStations(searchLineResponse, 2,"강남역","광교역");
    }

    @Test
    @DisplayName("노선에 없는 역 제거")
    public void removeHasNotStationInLineTest() {
        StationResponse suwonStation = StationAcceptanceTestRequest.createStation("수원역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), suwonStation.getId(), 3);

        LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());

        StationResponse ewhaStation = StationAcceptanceTestRequest.createStation("이대역").as(StationResponse.class);

        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.removeStation(lineUri, ewhaStation.getId());

        LineSectionAcceptanceTestResponse.isStatusBadRequest(response);
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        checkValidCompareSearchStations(searchLineResponse, 3,"강남역","수원역","광교역");
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 역 제거")
    public void removeHasLastOneStationInLineTest() {
        ExtractableResponse<Response> response = LineSectionAcceptanceTestRequest.removeStation(lineUri, gangNamStation.getId());
        LineSectionAcceptanceTestResponse.isStatusBadRequest(response);
    }

    private void checkValidCompareSearchStations(ExtractableResponse response, int sectionSize,String... stationNames) {
        LineResponse searchedLineResponse = response.jsonPath()
                .getObject(".", LineResponse.class);

        List<StationResponse> searchResponse = searchedLineResponse.getStations();
        assertThat(searchResponse.size()).isEqualTo(sectionSize);
        assertThat(searchResponse.stream()
                .map(it -> it.getName())).containsExactly(stationNames);
    }

    private void checkValidCompareStations(ExtractableResponse response) {
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        LineResponse searchedLineResponse = searchLineResponse.jsonPath()
                .getObject(".", LineResponse.class);

        LineResponse foundResponses = response.jsonPath()
                .getObject(".", LineResponse.class);

        List<StationResponse> addedSectionResponse = foundResponses.getStations();
        List<StationResponse> searchResponse = searchedLineResponse.getStations();

        compareStation(addedSectionResponse, searchResponse);
    }

    private void compareStation(List<StationResponse> sectionResponse, List<StationResponse> searchResponse) {
        for (int i = 0; i < sectionResponse.size(); i++) {
            StationResponse createStation = sectionResponse.get(i);
            StationResponse selectStation = searchResponse.get(i);
            assertThat(createStation.getId()).isEqualTo(selectStation.getId());
        }
    }
}
