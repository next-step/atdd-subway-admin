package nextstep.subway.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    TestEntityManager em;

    Station station1 = null;
    Station station2 = null;

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

    @DisplayName("페치조인으로 Sections 까지 한번에 가져온다")
    @Test
    void findWithSectionsById() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        Line line = new Line("신분당선", "bg-red-600");
        line.addSection(10, station1, station2);
        Line saveLine = lineRepository.save(line);
        flushAndClear();

        Line findLine = lineRepository.findWithSectionsById(saveLine.getId()).get();

        assertThat(findLine.getSectionList()).hasSize(1);
    }

    private Line getLine() {
        return new Line("신분당선", "bg-red-600");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
