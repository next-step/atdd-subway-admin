package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void save() {
        // when
        Line line = lineRepository.save(new Line("2호선", "green"));

        // then
        assertThat(line).isNotNull();
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("중복된 이름의 노선을 등록 하는 경우")
    @Test
    void saveWithNameAlreadyExist() {
        // given
        Line line = lineRepository.save(new Line("2호선", "green"));

        // when
        assertThatThrownBy(() -> {
            lineRepository.save(new Line("2호선", "green"));
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void findAll() {
        // given
        lineRepository.save(new Line("2호선", "green"));
        lineRepository.save(new Line("5호선", "purple"));

        // when
        List<Line> lines = lineRepository.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        Line expected = lineRepository.save(new Line("5호선", "purple"));

        // when
        Line actual = lineRepository.findById(expected.getId()).orElse(null);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual == expected).isTrue();
    }

}