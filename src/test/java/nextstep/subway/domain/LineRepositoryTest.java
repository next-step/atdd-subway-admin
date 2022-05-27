package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository repository;

    @Test
    void 생성() {
        // when
        Line saved = repository.save(new Line("2호선", "#009D3E", 10));

        // then
        assertThat(saved).isNotNull();
    }

    @Test
    void 조회() {
        // given
        Line saved = repository.save(new Line("2호선", "#009D3E", 10));

        // when
        Optional<Line> line = repository.findById(saved.getId());

        // then
        assertThat(line.isPresent()).isTrue();
    }

    @Test
    void 수정() {
        // given
        Line saved = repository.save(new Line("2호선", "#009D3E", 10));

        // when
        saved.updateDistance(100);

        // then
        Optional<Line> line = repository.findById(saved.getId());
        assertThat(line.get().getDistance()).isEqualTo(100);
    }

    @Test
    void 삭제() {
        // given
        Line line = repository.save(new Line("2호선", "#009D3E", 10));

        // when
        repository.delete(line);

        // then
        Optional<Line> actual = repository.findById(line.getId());
        assertThat(actual.isPresent()).isFalse();
    }
}
