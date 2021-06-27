package nextstep.subway.line.application;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("노선 서비스 테스트")
class LineServiceTest extends AcceptanceTest {

    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    private static StationResponse 강남역;
    private static StationResponse 역삼역;
    private static StationResponse 서울대입구역;
    private static StationResponse 신도림역;
    private static StationResponse 사당역;
    private static StationResponse 영등포구청역;

    private static LineResponse 죠르디_선;

    private static final int 기본_구간_거리 = 100;
    private static final int 구간_사이_추가된_구간간_거리 = 30;

    @BeforeEach
    void setBasicDomainData() {
        강남역 = stationService.saveStation(new StationRequest("강남역"));
        역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        서울대입구역 = stationService.saveStation(new StationRequest("서울대입구역"));
        신도림역 = stationService.saveStation(new StationRequest("신도림역"));
        사당역 = stationService.saveStation(new StationRequest("사당역"));
        영등포구청역 = stationService.saveStation(new StationRequest("영등포구청역"));

        죠르디_선 = lineService.saveLine(new LineRequest("죠르디", "초록", 강남역.getId(), 역삼역.getId(), 기본_구간_거리));
    }


    @Test
    void addSection_성공_최상행_등록() {
        // given
        SectionRequest givenRequest = new SectionRequest(죠르디_선.getId(), 서울대입구역.getId(), 강남역.getId(), 기본_구간_거리);

        // when, then
        assertDoesNotThrow(() -> lineService.addSection(givenRequest));
    }

    @Test
    void addSection_성공_최하행_등록() {
        // given
        SectionRequest givenRequest = new SectionRequest(죠르디_선.getId(), 역삼역.getId(), 서울대입구역.getId(), 기본_구간_거리);

        // when, then
        assertDoesNotThrow(() -> lineService.addSection(givenRequest));
    }

    @Test
    void addSection_성공_구간_중간_등록1() {
        // given
        SectionRequest givenRequest = new SectionRequest(죠르디_선.getId(), 강남역.getId(), 서울대입구역.getId(), 구간_사이_추가된_구간간_거리);

        // when, then
        assertDoesNotThrow(() -> lineService.addSection(givenRequest));
    }

    @Test
    void addSection_성공_구간_중간_등록2() {
        // given
        SectionRequest givenRequest = new SectionRequest(죠르디_선.getId(), 서울대입구역.getId(), 역삼역.getId(), 구간_사이_추가된_구간간_거리);

        // when, then
        assertDoesNotThrow(() -> lineService.addSection(givenRequest));
    }
}