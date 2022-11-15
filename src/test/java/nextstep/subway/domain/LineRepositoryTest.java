package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    LineRepository repository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    TestEntityManager em;

    Station station1 = null;
    Station station2 = null;

    @Test
    void save() {
        Line expected = getLineRequest();
        Line actual = repository.save(expected);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        Line actual = repository.save(getLineRequest());
        repository.deleteById(actual.getId());
        flushAndClear();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> repository.findById(actual.getId()).get());
    }

    @Test
    void update() {
        Line saveLine = repository.save(getLineRequest());
        saveLine.changeInformation("신분당선2", "bg-green-600");
        flushAndClear();
        Line findLine = repository.findById(saveLine.getId()).get();
        assertThat(findLine.getName()).isEqualTo("신분당선2");
        assertThat(findLine.getColor()).isEqualTo("bg-green-600");
    }

    private Line getLineRequest() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        return new Line("신분당선", "bg-red-600", 10, station1, station2);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
