package nextstep.subway.line;

import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Color;
import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선")
public
class LineTest {

    @DisplayName("노선 생성")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Line(new Name(신분당선_이름), new Color(신분당선_색상)));
    }

    @DisplayName("노선 수정")
    @Test
    void update() {
        Line line = new Line(new Name(신분당선_이름), new Color(신분당선_색상));
        line.update(new Name(이호선_이름), new Color(이호선_색상));
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(new Name(이호선_이름)),
                () -> assertThat(line.getColor()).isEqualTo(new Color(이호선_색상))
        );
    }
}
