package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameLineObject() {
        final Station gangNamStation = new Station("강남역");
        final Station yangJaeStation = new Station("양재역");
        Line line = new Line("신분당선", "bg-red-600", gangNamStation, yangJaeStation, 10L);

        assertThat(line).isEqualTo(new Line("신분당선", "bg-red-600", gangNamStation, yangJaeStation, 10L));
    }
}
