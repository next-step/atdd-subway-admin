package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    StationService service;

    @Test
    void saveStation() {
        StationResponse expected = new StationResponse(null, "경기 광주역", null, null);

        Long id = service.saveStation(new StationRequest("경기 광주역"));

        StationResponse findStation = service.findResponseById(id);
        assertThat(findStation.getName()).isEqualTo(expected.getName());
    }

    @Test
    void findAllStations() {
        service.saveStation(new StationRequest("경기 광주역"));
        service.saveStation(new StationRequest("중앙역"));

        List<StationResponse> allStations = service.findAllStations();

        assertThat(allStations).hasSize(2);
    }

    @Test
    void deleteStationById() {
        Long id = service.saveStation(new StationRequest("경기 광주역"));

        service.deleteStationById(id);

        List<StationResponse> allStations = service.findAllStations();
        assertThat(allStations).hasSize(0);
    }
}
