package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.HashSet;

import static nextstep.subway.line.LineAcceptanceTest.노선을_생성한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 유닛테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionUnitTest {
    @LocalServerPort
    int port;

    @Autowired
    private StationService stationService;

    private StationResponse 건대역;
    private StationResponse 뚝섬유원지역;
    private StationResponse 청담역;
    private StationResponse 강남구청역;

    private Station 건대;
    private Station 뚝섬유원지;
    private Station 청담;
    private Station 강남구청;

    private LineResponse 칠호선;

    Section 구간;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // given
        건대역 = 지하철역을_생성한다("건대역").as(StationResponse.class);
        뚝섬유원지역 = 지하철역을_생성한다("뚝섬유원지역").as(StationResponse.class);
        청담역 = 지하철역을_생성한다("청담역").as(StationResponse.class);
        강남구청역 = 지하철역을_생성한다("강남구청역").as(StationResponse.class);

        칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 건대역.getId(), 뚝섬유원지역.getId(), 10).as(LineResponse.class);

        건대 = stationService.findById(건대역.getId());
        뚝섬유원지 = stationService.findById(뚝섬유원지역.getId());
        청담 = stationService.findById(청담역.getId());
        강남구청 = stationService.findById(강남구청역.getId());

        구간 = new Section(건대, 뚝섬유원지, 20);
    }

    @Test
    @DisplayName("기존구간에 새로운 구간 추가")
    void updateUpSection() {
        //given
        Station 청담 = stationService.findById(뚝섬유원지역.getId());
        Section new구간 = new Section(건대, 청담, 10);

        //when
        구간.updateUpSection(new구간);

        //then
        assertThat(구간.getDownStation()).isEqualTo(청담);
        assertThat(구간.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("구간의 상행역 비교")
    void getEqualsUpStation() {
        assertThat(구간.getEqualsUpStation(건대)).isTrue();
    }

    @Test
    @DisplayName("상행과 하행이 모두 포함")
    void isUpAndDownStationContains() {
        assertThat(구간.isUpAndDownStationContains(new HashSet<>(Arrays.asList(건대, 뚝섬유원지)))).isTrue();
        assertThat(구간.isUpAndDownStationContains(new HashSet<>(Arrays.asList(건대, 청담)))).isFalse();
    }

    @Test
    @DisplayName("상행과 하행이 모두 미포함")
    void isUpAndDownStationNotContains() {
        assertThat(구간.isUpAndDownStationNotContains(new HashSet<>(Arrays.asList(강남구청, 청담)))).isTrue();
        assertThat(구간.isUpAndDownStationNotContains(new HashSet<>(Arrays.asList(건대, 청담)))).isFalse();
    }
}
