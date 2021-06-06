package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 response 테스트")
class LineResponseTest {

    @Test
    @DisplayName("Line entity를 이용하여 지하철 노선 response 생성")
    void of() {
        Line line = new Line("신분당선", "bg-red-600");
        LineResponse lineResponse = LineResponse.of(line);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @Test
    @DisplayName("optional로 포장된 Line entity가 비어있을때 에러 발생")
    void optional_of() {
        Optional<Line> emptyLine = Optional.empty();
        assertThatThrownBy(() -> LineResponse.of(emptyLine)).isInstanceOf(NoSuchElementException.class);

    }
}
