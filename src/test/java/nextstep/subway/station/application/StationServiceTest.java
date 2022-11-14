package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public
class StationServiceTest {

    public static StationRequest 강남역 = new StationRequest("강남역");
    public static StationRequest 역삼역 = new StationRequest("역삼역");
    public static StationRequest 교대역 = new StationRequest("교대역");
    public static StationRequest 마두역 = new StationRequest("마두역");
    public static StationRequest 서초역 = new StationRequest("서초역");

    @Autowired
    private StationRepository stationRepository;

    @Test
    @Transactional
    void saveStation() {
        Station gangnam = stationRepository.save(강남역.toStation());
        assertThat(gangnam.getId()).isNotNull();
    }

    @Test
    @Transactional
    void findAllStations() {
        stationRepository.save(강남역.toStation());
        stationRepository.save(역삼역.toStation());
        stationRepository.save(교대역.toStation());
        stationRepository.save(서초역.toStation());
        assertThat(stationRepository.findAll()).hasSize(4);
    }

    @Test
    @Transactional
    void findStationById() {
        Station expect = stationRepository.save(서초역.toStation());
        assertThat(stationRepository.findById(expect.getId()).get()).isEqualTo(expect);
    }

    @Test
    @Transactional
    void deleteStationById() {
        Station expect = stationRepository.save(서초역.toStation());
        stationRepository.delete(expect);
        assertThat(stationRepository.findById(expect.getId()).isPresent()).isFalse();
    }

}
