package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void 지하철_노선_저장() {
        Line line = new Line("신분당선", "bg-red-600");
        Line result = lineRepository.save(line);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(line.getName()),
                () -> assertThat(result.getColor()).isEqualTo(line.getColor())
        );
    }

    @Test
    void 지하철_노선_목록_조회() {
        lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineRepository.save(new Line("분당선", "bg-green-600"));

        assertThat(lineRepository.findAll()).hasSize(2);
    }

    @Test
    void 지하철_노선_단건_조회() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineRepository.findById(line.getId())).isNotEmpty();
    }

    @Test
    void 지하철_노선_삭제() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineRepository.deleteById(line.getId());

        assertThat(lineRepository.findById(line.getId())).isEmpty();
    }
}
