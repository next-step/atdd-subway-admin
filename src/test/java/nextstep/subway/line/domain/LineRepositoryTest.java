package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("노선을 등록한다.")
    @Test
    void save_노선등록() {
        final Line actual = 노선_등록("2호선", "green");

        final Line expected = lineRepository.save(actual);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("등록한 노선을 조회한다.")
    @Test
    void findById_노선조회() {
        final Line actual = 노선_등록_저장("2호선", "green");

        final Line expected = 노선_조회(actual);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("등록한 노선들을 조회한다.")
    @Test
    void findAll_노선들조회() {
        final Line actual1 = 노선_등록_저장("1호선", "navy");
        final Line actual2 = 노선_등록_저장("2호선", "green");

        final List<Line> lineList = 노선들_조회();

        assertAll(
                () -> assertThat(lineList).hasSize(2),
                () -> assertThat(lineList.get(0).getName()).isEqualTo(actual1.getName()),
                () -> assertThat(lineList.get(0).getColor()).isEqualTo(actual1.getColor()),
                () -> assertThat(lineList.get(1).getName()).isEqualTo(actual2.getName()),
                () -> assertThat(lineList.get(1).getColor()).isEqualTo(actual2.getColor())
        );
    }

    @DisplayName("등록한 노선을 수정한다.")
    @Test
    void update_노선수정() {
        final Line actual = 노선_등록_저장("2호선", "green");

        final Line expected = 노선_등록("분당선", "yellow");

        노선_수정_저장(actual, expected);

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(expected.getColor())
        );
    }

    @DisplayName("등록한 노선을 삭제한 후 조회 시 예외가 발생한다.")
    @Test
    void delete_예외_노선삭제() {
        final Line actual = 노선_등록_저장("2호선", "green");

        노선_삭제(actual);

        assertThatThrownBy(() -> 노선_조회(actual))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    private void 노선_수정_저장(Line origin, Line change) {
        origin.update(change);
    }

    private Line 노선_조회(Line line) {
        return lineRepository.findById(line.getId()).get();
    }

    private List<Line> 노선들_조회() {
        return lineRepository.findAll();
    }

    private Line 노선_등록(String name, String color) {
        return Line.of(name, color);
    }

    private Line 노선_등록_저장(String name, String color) {
        return lineRepository.save(Line.of(name, color));
    }

    private void 노선_삭제(Line actual) {
        lineRepository.delete(actual);
    }
}
