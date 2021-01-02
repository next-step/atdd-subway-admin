//package nextstep.subway.section;
//
//import nextstep.subway.AcceptanceTest;
//import nextstep.subway.station.StationAcceptanceTest;
//import nextstep.subway.station.domain.Station;
//import nextstep.subway.station.dto.StationResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//public class SectionAcceptanceTest extends AcceptanceTest {
//
//    private Station 강남역;
//    pri
//
//    @BeforeEach
//    public void setUp() {
//        super.setUp();
//
//        // given
//        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
//        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
//
//        createParams = new HashMap<>();
//        createParams.put("name", "신분당선");
//        createParams.put("color", "bg-red-600");
//        createParams.put("upStation", 강남역.getId() + "");
//        createParams.put("downStation", 광교역.getId() + "");
//        createParams.put("distance", 10 + "");
//        신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
//    }
//
//    @DisplayName("지하철 구간을 등록한다.")
//    @Test
//    public void addSection() {
//
//    }
//
//}
