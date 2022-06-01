package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void 노선이_insert_되면_아이디가_부여되고_아이디로_select_할_수_있어야_한다() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        final Line insertedLine = lineRepository.save(line);

        // then
        assertThat(insertedLine).isEqualTo(line);
        assertThat(insertedLine).isEqualTo(lineRepository.findById(insertedLine.getId()).get());
    }
}
