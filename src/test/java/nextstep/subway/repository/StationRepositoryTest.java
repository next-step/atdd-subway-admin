package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    void save() {
        // given
        final Station expected = new Station("강남역");

        // when
        final Station actual = stationRepository.save(expected);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById() {
        // given
        final Station expected = stationRepository.save(new Station("강남역"));

        // when
        final Station actual = stationRepository.findById(expected.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll() {
        // given
        stationRepository.save(new Station("강남역"));
        stationRepository.save(new Station("잠실역"));

        // when
        final List<Station> actual = stationRepository.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void delete() {
        // given
        final Station expected = stationRepository.save(new Station("강남역"));

        // when
        stationRepository.delete(expected);
        final Optional<Station> actual = stationRepository.findById(expected.getId());

        // then
        assertThat(actual).isEmpty();
    }

}
