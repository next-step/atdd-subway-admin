package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * StationRepository 기능 검증 테스트
 */
@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("지하철 역 목록 저장")
    void create_all() {
        // given
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"));

        // when
        List<Station> saveStations = this.stationRepository.saveAll(stations);

        // then
        assertThat(saveStations).containsAll(stations);
    }

    @Test
    @DisplayName("중복된 이름의 역 저장 실패")
    void duplicate_station_exception() {
        // given
        Station firstStation = new Station("강남역");
        Station secondStation = new Station("강남역");
        this.stationRepository.save(firstStation);

        // when
        assertThatThrownBy(() -> this.stationRepository.save(secondStation))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("전체 저장 시 동일한 역 이름이 있을 경우 실패")
    void create_all_duplicate_exception() {
        // given
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("강남역"));

        // when
        assertThatThrownBy(() -> this.stationRepository.saveAll(stations))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
