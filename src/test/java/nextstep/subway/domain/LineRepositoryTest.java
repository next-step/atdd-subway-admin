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
    TestEntityManager em;

    @Test
    void save() {
        Line expected = new Line("신분당선", "bg-red-600", 10, "1", "2");
        Line actual = repository.save(expected);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        Line actual = repository.save(new Line("신분당선", "bg-red-600", 10, "1", "2"));
        repository.deleteById(actual.getId());
        flushAndClear();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> repository.findById(actual.getId()).get());
    }

    @Test
    void update() {
        Line saveLine = repository.save(new Line("신분당선", "bg-red-600", 10, "1", "2"));
        saveLine.changeInformation("신분당선2", "bg-green-600", 20, "2", "3");
        flushAndClear();
        Line findLine = repository.findById(saveLine.getId()).get();
        assertThat(findLine.getName()).isEqualTo("신분당선2");
        assertThat(findLine.getColor()).isEqualTo("bg-green-600");
        assertThat(findLine.getDistance()).isEqualTo(20);
        assertThat(findLine.getUpStationId()).isEqualTo("2");
        assertThat(findLine.getDownStationId()).isEqualTo("3");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
