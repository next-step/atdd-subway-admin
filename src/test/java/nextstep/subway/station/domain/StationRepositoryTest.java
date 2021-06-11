package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationRepositoryTest {
    @Autowired
    StationRepository stationRepository;

    @Test
    @DisplayName("쿼리 생성하여 역 리스트를 가져오는지 체크")
    void findStations() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        //when
        List<Station> stations = stationRepository.findStations(Arrays.asList(강남역.getId()));
        //then
        assertThat(stations.size()).isEqualTo(1);
    }
}