package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
    }

    @Test
    void 노선_생성() {
        assertAll(
                () -> assertThat(line).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo("2호선"),
                () -> assertThat(line.getColor()).isEqualTo("green")
        );
    }

    @Test
    void 노선_초기화() {
        LineStation 상행_종점 = new LineStation(null, new Station("상행종점역"), 10, line);
        LineStation 하행_종점 = new LineStation(new Station("하행종점역"), null, 10, line);
        assertThatNoException().isThrownBy(
                () -> line.initLineStations(Arrays.asList(상행_종점, 하행_종점))
        );
    }

    @Test
    void 노선_정보_수정() {
        line.update("3호선", "orange");
        assertAll(
                () -> assertThat(line).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo("3호선"),
                () -> assertThat(line.getColor()).isEqualTo("orange")
        );
    }
}
