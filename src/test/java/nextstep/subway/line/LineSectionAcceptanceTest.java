package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.*;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    LineResponse shinBunDangLine;
    StationResponse gangNamStation;
    StationResponse gwangKyoStation;
    String lineUri;
    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        gangNamStation = StationAcceptanceTest.createStation("강남역").as(StationResponse.class);
        gwangKyoStation = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600"
                , gangNamStation.getId(), gwangKyoStation.getId(), 10);

        lineUri = LineAcceptanceTestRequest.createLineRequest(lineRequest).header("Location");
    }

    @Test
    @DisplayName("상행 종점에 section추가")
    public void insertUpstationSectionTest() {
        StationResponse shinNonHyunStation = StationAcceptanceTest.createStation("신논현역").as(StationResponse.class);
        LineSectionCreateRequest lineSectionCreateRequest =
                new LineSectionCreateRequest(gangNamStation.getId(), shinNonHyunStation.getId(), 3);

        ExtractableResponse response = LineSectionAcceptanceTestRequest.addSectionInLine(lineUri, lineSectionCreateRequest.getUpStationId(),
                lineSectionCreateRequest.getDownStationId(), lineSectionCreateRequest.getDistance());
       LineAcceptanceTestResponse.isStatusOk(response);
    }


}
