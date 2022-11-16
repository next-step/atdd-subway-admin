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
    LineRepository lineRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void save() {
        Line expected = getLine();
        Line actual = lineRepository.save(expected);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        Line actual = lineRepository.save(getLine());
        lineRepository.deleteById(actual.getId());
        flushAndClear();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> lineRepository.findById(actual.getId()).get());
    }

    @Test
    void change() {
        Line saveLine = lineRepository.save(getLine());
        saveLine.change("신분당선2", "bg-green-600");
        flushAndClear();
        Line findLine = lineRepository.findById(saveLine.getId()).get();
        assertThat(findLine.getName()).isEqualTo("신분당선2");
        assertThat(findLine.getColor()).isEqualTo("bg-green-600");
    }

    private Line getLine() {
        return new Line("신분당선", "bg-red-600");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
