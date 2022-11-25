package nextstep.subway.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    StationRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    void save() {
        Station expected = new Station("경기 광주역");

        Station actual = repository.save(expected);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        Station station = repository.save(new Station("경기 광주역"));

        repository.deleteById(station.getId());
        flushAndClear();

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> repository.findById(station.getId()).get());
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
