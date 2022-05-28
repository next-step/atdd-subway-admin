package nextstep.subway.station;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Test
    void 지하철역_저장() {
        Station station = new Station("강남역");
        Station result = stationRepository.save(station);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(station.getName())
        );
    }

    @Test
    void 지하철역_이름_중복_저장_예외발생() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("강남역");
        stationRepository.save(station1);

        assertThatThrownBy(() -> stationRepository.save(station2)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 지하철역_목록_조회() {
        stationRepository.save(new Station("강남역"));
        stationRepository.save(new Station("역삼역"));

        assertThat(stationRepository.findAll()).hasSize(2);
    }

    @Test
    void 지하철역_삭제() {
        Station station = stationRepository.save(new Station("강남역"));
        stationRepository.deleteById(station.getId());

        assertThat(stationRepository.findById(station.getId())).isEmpty();
    }
}
