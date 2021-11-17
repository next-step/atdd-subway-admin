package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:scripts/lineInsert.sql")
public class LineTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("라인 생성 성공")
    public void createLine() {
        Line line2 = new Line("2호선", "green");
        Line save = lineRepository.save(line2);

        assertAll(() -> {
            assertThat(save.getName()).isEqualTo(line2.getName());
            assertThat(save.getColor()).isEqualTo(line2.getColor());
        });
    }

    @Test
    @DisplayName("라인 조회")
    public void getLine() {
        Optional<Line> line = lineRepository.findById(1L);
        assertThat(line.isPresent()).isTrue();
    }

    @Test
    @DisplayName("라인 목록 조회")
    public void getAllLines() {
        lineRepository.save(new Line("3호선", "orange"));
        List<Line> lines = lineRepository.findAll();
        assertThat(lines.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("라인 이름,색상 수정")
    public void updateLine() {
        Optional<Line> optionalLine = lineRepository.findById(1L);

        assertThat(optionalLine.isPresent()).isTrue();

        Line line = optionalLine.get();
        line.update(new Line("2호선", "green"));

        Line save = lineRepository.save(line);
        //쿼리 확인
        lineRepository.flush();

        assertAll(() -> {
            assertThat(save.getName()).isEqualTo("2호선");
            assertThat(save.getColor()).isEqualTo("green");
        });
    }

    @Test
    @DisplayName("라인 삭제")
    public void deleteLine() {
        Line save = lineRepository.save(new Line("2호선", "green"));

        Optional<Line> optionalLine1 = lineRepository.findById(save.getId());
        assertThat(optionalLine1.isPresent()).isTrue();

        lineRepository.delete(optionalLine1.get());

        //쿼리 확인
        lineRepository.flush();

        Optional<Line> optionalLine2 = lineRepository.findById(save.getId());
        assertThat(optionalLine2.isPresent()).isFalse();

    }


}
