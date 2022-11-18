package nextstep.subway.section;

import nextstep.subway.AbstractAcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static nextstep.subway.line.LineAcceptanceTest.requestApiByCreateLine;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 교대역;
    private StationResponse 강남역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        교대역 = StationAcceptanceTest.createStation("교대역").extract().as(StationResponse.class);
        강남역 = StationAcceptanceTest.createStation("강남역").extract().as(StationResponse.class);

        신분당선 = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)).extract().as(LineResponse.class);
    }

}
