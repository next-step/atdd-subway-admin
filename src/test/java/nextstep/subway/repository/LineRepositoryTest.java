package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import nextstep.subway.domain.Line;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    void save() {
        // given
        final Line expected = new Line("2호선", "green");

        // when
        final Line actual = lineRepository.save(expected);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById() {
        // given
        final Line expected = lineRepository.save(new Line("2호선", "green"));

        // when
        final Line actual = lineRepository.findById(expected.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void update() {
        // given
        final Line expected = lineRepository.save(new Line("2호선", "green"));

        // when
        expected.changeLineInfo("3호선", "orange");
        final Line actual = lineRepository.findById(expected.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(actual.getName()).isEqualTo("3호선");
    }

    @Test
    void delete() {
        // given
        final Line expected = lineRepository.save(new Line("2호선", "green"));

        // when
        lineRepository.delete(expected);
        final Optional<Line> actual = lineRepository.findById(expected.getId());

        // then
        assertThat(actual).isEmpty();
    }
}
