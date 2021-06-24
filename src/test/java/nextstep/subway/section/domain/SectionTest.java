package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class SectionTest {

    private static final int 거리 = 10;
    private static final Station 신도림역 = new Station(1L, "신도림역");
    private static final Station 서울역 = new Station(2L, "서울역");
    private static final Line 지하철_1호선 = new Line("1호선", "남색", 신도림역, 서울역, 거리);

    @DisplayName("상행역과 하행역이 같아 노선을 생성을 실패한다")
    @Test
    void create_SameStations_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Section(지하철_1호선, 신도림역, 신도림역, 거리)
        );
    }

    @DisplayName("거리가 1보다 작아 노선을 생성을 실패한다")
    @Test
    void create_TooSmall_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Section(지하철_1호선, 신도림역, 서울역, 0)
        );
    }

    @DisplayName("모든 역 정보를 상행에서 하행으로 반환한다")
    @Test
    void stations() {
        Section section = new Section(지하철_1호선, 신도림역, 서울역, 거리);
        assertThat(section.stations()).isEqualTo(Arrays.asList(신도림역, 서울역));
    }
}
