package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 구간역")
public class LineStationTest {


    @Test
    @DisplayName("노선은 필수이다.")
    void isNotNullLine() {
        Station station = new Station("주안역");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new LineStation(null, station));

    }

    @Test
    @DisplayName("역은 필수이다.")
    void isNotNullStation() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new LineStation(new Line("1호선", "파란색", 10),null));

    }
}
