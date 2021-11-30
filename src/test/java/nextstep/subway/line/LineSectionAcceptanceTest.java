package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.*;
import nextstep.subway.station.StationAcceptanceTestRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private LineResponse shinBunDangLine;
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
        checkValid(response);
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
        checkValid(response);
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
        checkValid(response);
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
        checkValid(response);
    }

    private void checkValid(ExtractableResponse addedResponse) {
        ExtractableResponse<Response> searchLineResponse = LineAcceptanceTestRequest.selectOneLine(lindId);

        LineResponse searchedLineResponse = searchLineResponse.jsonPath()
                .getObject(".", LineResponse.class);

        LineResponse addedResponses = addedResponse.jsonPath()
                .getObject(".", LineResponse.class);

        List<StationResponse> addedSectionResponse = addedResponses.getStations();
        List<StationResponse> searchResponse = searchedLineResponse.getStations();

        compareStation(addedSectionResponse, searchResponse);
    }

    private void compareStation(List<StationResponse> addSectionResponse, List<StationResponse> searchResponse) {
        for (int i = 0; i < addSectionResponse.size(); i++) {
            StationResponse createStation = addSectionResponse.get(i);
            StationResponse selectStation = searchResponse.get(i);
            assertThat(createStation.getId()).isEqualTo(selectStation.getId());
        }
    }
}
