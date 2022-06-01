package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Station A역;
    private Station B역;

    @BeforeEach
    void setUp() {
        A역 = new Station(1L, "A역");
        B역 = new Station(1L, "B역");
    }

    @Test
    void 노선_초기화_테스트() {
        // given
        Line line = new Line("2호선", "초록", 10);

        // when
        line.initStation(A역, B역);

        // then
        assertThat(line.getUpStation()).isEqualTo(A역);
        assertThat(line.getDownStation()).isEqualTo(B역);
        assertThat(line.getDistance()).isEqualTo(10);
        assertThat(line.getSections()).isNotNull();
        assertThat(line.getLineStations()).isNotNull();
    }
}
