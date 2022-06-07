package nextstep.subway.station;

import io.restassured.RestAssured;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.SectionStations;
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

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

public class SectionStationsUnitTest {
    @LocalServerPort
    int port;

    @Autowired
    private StationRepository stationRepository;

    List<Station> stations;
    StationResponse 청담역;
    StationResponse 강남구청역;
    LineResponse 칠호선;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        청담역 = 지하철역을_생성한다("청담역").as(StationResponse.class);
        강남구청역 = 지하철역을_생성한다("강남구청역").as(StationResponse.class);
        stations = stationRepository.findAll();
    }

    @Test
    public void 전체_역목록에서_조회할_up_down_역조회() {
        SectionStations sectionStations = new SectionStations();
        sectionStations.findUpAndDownStations(stations, 청담역.getId(), 강남구청역.getId());
        assertThat(sectionStations.getUpStation().getId()).isEqualTo(청담역.getId());
        assertThat(sectionStations.getDownStation().getId()).isEqualTo(강남구청역.getId());
    }
}
