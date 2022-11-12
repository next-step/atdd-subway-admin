package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class StationCommandServiceTest {
    @Autowired
    private StationCommandService stationCommandService;

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("지하철역 생성")
    @Test
    void saveStation() {
        StationRequest stationRequest = StationRequest.from("강남역");

        StationResponse stationResponse = stationCommandService.saveStation(stationRequest);

        assertAll(
                () -> assertThat(stationResponse.getId()).isNotNull(),
                () -> assertThat(stationResponse.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("지하철역 삭제")
    @Test
    void deleteStationById() {
        StationRequest stationRequest = StationRequest.from("강남역");
        StationResponse stationResponse = stationCommandService.saveStation(stationRequest);

        stationCommandService.deleteStationById(stationResponse.getId());

        Optional<Station> findStation = stationRepository.findById(stationResponse.getId());

        assertThat(findStation).isNotPresent();
    }
}
