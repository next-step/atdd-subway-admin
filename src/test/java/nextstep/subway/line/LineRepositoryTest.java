package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    LineRepository lineRepository;

    @Test
    @DisplayName("Line Save() 후 id not null 체크")
    void save() {
        // given
        // when
        Line persistLine = lineRepository.save(LineTest.LINE1);

        // then
        assertNotNull(persistLine.getId());
    }

    @Test
    @DisplayName("같은 인스턴스 변수 값의 Line1,Line2 가 주어질때, 영속객체와 일치,불일치 검증")
    void equals() {
        // given
        Line line1 = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        Line line2 = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);

        // when
        Line persistLine = lineRepository.save(line1);

        // then
        assertAll(
            () -> assertEquals(persistLine, line1),
            () -> assertNotEquals(persistLine, line2)
        );
    }

    @Test
    @DisplayName("Line 업데이트 후 변경된 name,color 일치 체크")
    void update() {
        // given
        Line persistLine = lineRepository.save(LineTest.LINE2);

        // when
        persistLine.update(LineTest.LINE2);
        Line actual = lineRepository.findByName(persistLine.getName()).get();

        // when
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LineTest.LINE2.getName()),
            () -> assertThat(actual.getColor()).isEqualTo(LineTest.LINE2.getColor())
        );
    }

    @Test
    @DisplayName("Line 2개 저장 후, findAll 조회시 size 2개 검증")
    void findAll() {
        // given
        lineRepository.save(new Line("10호선", "color100"));
        lineRepository.save(new Line("11호선", "color100"));

        // when
        List<Line> actual = lineRepository.findAll();

        // then
        assertThat(actual).hasSize(2);
    }


    @Test
    @DisplayName("Line 2개 저장, 1개삭제 후 findAll 조회시 size 1개 검증")
    void delete_after_findAll() {
        // given
        lineRepository.save(new Line("10호선", "color100"));
        Line deleteLine = lineRepository.save(new Line("11호선", "color100"));

        // when
        deleteLine.delete();
        List<Line> actual = lineRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("soft delete Flush 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Line persistLine = lineRepository.save(LineTest.LINE1);
        persistLine.delete();

        // when
        Line actual = lineRepository.findById(persistLine.getId()).get();

        // then
        assertThat(actual.isDeleted()).isTrue();
    }
}
