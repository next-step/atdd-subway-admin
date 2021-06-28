package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class LineTest {

    private static final int 거리 = 10;
    private static final Station 신도림역 = new Station(1L, "신도림역");
    private static final Station 서울역 = new Station(2L, "서울역");

    @DisplayName("이름이 빈 값이면 생성을 실패한다")
    @Test
    void create_EmptyName_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line(null, "남색", 신도림역, 서울역, 거리)
        );
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line("", "남색", 신도림역, 서울역, 거리)
        );
    }

    @DisplayName("색깔이 빈 값이면 생성을 실패한다")
    @Test
    void create_EmptyColor_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line("1호선", null, 신도림역, 서울역, 거리)
        );
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line("1호선", "", 신도림역, 서울역, 거리)
        );
    }

    @DisplayName("상행역과 하행역이 같아 노선을 생성을 실패한다")
    @Test
    void create_SameStations_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line("1호선", "남색", 신도림역, 신도림역, 거리)
        );
    }

    @DisplayName("거리가 1보다 작아 노선을 생성을 실패한다")
    @Test
    void create_TooSmall_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Line("1호선", "남색", 신도림역, 서울역, 0)
        );
    }

    @DisplayName("모든 역 정보를 반환한다")
    @Test
    void getStations() {
        Line line = new Line("1호선", "남색", 신도림역, 서울역, 10);

        assertThat(line.getStations().size()).isEqualTo(2);
        assertThat(line.getStations().get(0).getName()).isEqualTo("신도림역");
    }

}
