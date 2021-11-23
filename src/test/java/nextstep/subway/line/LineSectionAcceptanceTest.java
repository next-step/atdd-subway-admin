package nextstep.subway.line;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public class LineSectionAcceptanceTest {

    @BeforeEach
    public void setUp() {
//        super.setUp();

        // given
        StationResponse gangNamStationResponse = StationAcceptanceTest.createStation("강남역").as(StationResponse.class);
        StationResponse GwangKyoStationResponse = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);

        Map createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStation", gangNamStationResponse.getId() + "");
        createParams.put("downStation", GwangKyoStationResponse.getId() + "");
        createParams.put("distance", 10 + "");
//        신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }

    private static LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
}
