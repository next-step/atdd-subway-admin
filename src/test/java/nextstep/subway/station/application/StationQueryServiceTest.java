package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class StationQueryServiceTest {
    @Autowired
    private StationQueryService stationQueryService;

    @Autowired
    private StationRepository stationRepository;

    @Test
    void findAllStations() {
        stationRepository.save(Station.from("강남역"));

        List<StationResponse> allStations = stationQueryService.findAllStations();

        assertAll(
                () -> assertThat(allStations).hasSize(1),
                () -> assertThat(allStations.get(0).getName()).isEqualTo("강남역")
        );
    }

    @Test
    void findById() {
        Station station = Station.from("강남역");
        stationRepository.save(station);

        Station findStation = stationQueryService.findById(station.getId());

        assertThat(findStation.getId()).isEqualTo(station.getId());
    }
}
