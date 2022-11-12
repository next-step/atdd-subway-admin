package nextstep.subway.station;

import static nextstep.subway.station.CreateFactory.지하철노선_등록_요청;
import static nextstep.subway.station.CreateFactory.지하철역_등록_요청;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;

public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철역_등록_요청("강남역").as(StationResponse.class);
        광교역 = 지하철역_등록_요청("광교역").as(StationResponse.class);

        신분당선 = 지하철노선_등록_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10L).as(LineResponse.class);
    }
}
