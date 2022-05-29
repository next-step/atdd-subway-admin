package nextstep.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    @Test
    void 지하철_노선_정보를_수정한다() {
        // given
        Line line = new Line("2호선", "green", new Section());

        // when
        line.update("4호선", "blue");

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("4호선"),
                () -> assertThat(line.getColor()).isEqualTo("blue")
        );
    }
}