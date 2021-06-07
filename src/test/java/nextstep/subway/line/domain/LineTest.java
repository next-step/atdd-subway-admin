package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line entity 테스트")
class LineTest {

    @Test
    @DisplayName("아이디 기준 노선 조회 결과가 없을 시 에러 정상 발생")
    void checkNullLine() {
        Optional<Line> emptyLine = Optional.empty();
        assertThatThrownBy(() -> Line.getNotNullLine(emptyLine)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 데이터 변경")
    void update() {
        Line expected = new Line("분당선", "bg-red-600");
        Line updateLine = new Line("구분당선", "bg-red-600");
        expected.update(updateLine);
        assertThat(expected).isEqualTo(updateLine);
    }
}
