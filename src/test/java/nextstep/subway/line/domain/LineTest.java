package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @DisplayName("노선의 이름과 컬러를 업데이트한다.")
    @Test
    void line_update() {
        Line line = new Line(1L, "2호선", "bg-green-600", new Sections());
        line.updateNameAndColor("5호선", "bg-red-600");

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("5호선"),
            () -> assertThat(line.getName()).isNotEqualTo("2호선"),
            () -> assertThat(line.getColor()).isEqualTo("bg-red-600"),
            () -> assertThat(line.getColor()).isNotEqualTo("bg-green-600")
        );
    }
}
