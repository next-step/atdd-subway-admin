package nextstep.subway.station;

import io.restassured.RestAssured;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.SectionStations;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("역 관련 인수테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationUnitTest {
    @LocalServerPort
    int port;

    @Autowired
    StationService stationService;

    private StationResponse 건대역;
    private StationResponse 뚝섬유원지역;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // given
        건대역 = 지하철역을_생성한다("건대역").as(StationResponse.class);
        뚝섬유원지역 = 지하철역을_생성한다("뚝섬유원지역").as(StationResponse.class);
    }

    @DisplayName("Id로 상행,하행역 조회")
    @Test
    void findUpDownStation() {
        SectionStations sectionStations = stationService.findUpDownStation(건대역.getId(), 뚝섬유원지역.getId());
        assertThat(sectionStations.getUpStation().getId()).isEqualTo(건대역.getId());
        assertThat(sectionStations.getDownStation().getId()).isEqualTo(뚝섬유원지역.getId());
    }

    @DisplayName("현재 존재하는 역목록 조회")
    @Test
    void findAllStations() {
        List<StationResponse> stations = stationService.findAllStations();
        assertThat(stations.size()).isEqualTo(2);
    }
}
