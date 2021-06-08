package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
class StationsTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("가")
    @Test
    void getStationById() {
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        stationRepository.save(강남역);
        stationRepository.save(잠실역);
        Stations stations = Stations.create(Arrays.asList(강남역, 잠실역));

        Station station = stations.getStationById(강남역.getId());
        Station findStation = stationRepository.findById(강남역.getId()).get();

        assertThat(station).isEqualTo(findStation);
    }

    @DisplayName("존재하지않는 역 조회")
    @Test
    void getStationByIdNotExist() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 잠실역 = new Station("잠실역");
        stationRepository.save(강남역);
        stationRepository.save(선릉역);
        stationRepository.save(잠실역);
        Stations stations = Stations.create(Arrays.asList(강남역, 잠실역));

        assertThatThrownBy(
                () -> stations.getStationById(선릉역.getId())
        ).isInstanceOf(NoSuchElementException.class);
    }
}