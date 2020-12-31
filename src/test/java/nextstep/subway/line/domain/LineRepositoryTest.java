package nextstep.subway.line.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void save() {
        Line line = lineRepository.save(new Line("2호선", "녹색"));

        assertThat(line).isNotNull();
        assertThat(line.getId()).isNotNull();
    }

    @Test
    void saveWithSameName() {
        lineRepository.save(new Line("2호선", "녹색"));

        assertThatThrownBy(() -> {
            lineRepository.save(new Line("2호선", "녹색"));
        }).isInstanceOf(RuntimeException.class);
    }
}
