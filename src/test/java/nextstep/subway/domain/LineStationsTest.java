package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineStationsTest {
    private final Line 이호선 = new Line("2호선", "green");
    private final Station 신도림역 = new Station("신도림역");
    private final Station 대림역 = new Station("대림역");
    private final Station 구로디지털단지역 = new Station("구로디지털단지역");
    private final LineStation 구간1 = new LineStation(신도림역, 대림역, 100, 이호선);
    private final LineStation 구간2 = new LineStation(대림역, 구로디지털단지역, 50, 이호선);
    private LineStations lineStations;

    @BeforeEach
    void setUp() {
        이호선.initLineStations(Arrays.asList(구간1, 구간2));
        lineStations = 이호선.getLineStation();
    }

    @Test
    @DisplayName("구간이 포함되었는지 여부를 조회한다.")
    void 구간_포함_여부_조회() {
        assertAll(
                () -> assertThat(lineStations.contains(new LineStation(신도림역, 대림역, 100, 이호선))).isTrue(),
                () -> assertThat(lineStations.contains(new LineStation(구로디지털단지역, 신도림역, 10, 이호선))).isFalse()
        );
    }

    @Test
    @DisplayName("역이 포함되었는지 여부를 조회한다.")
    void 역_포함_여부_조회() {
        assertAll(
                () -> assertThat(lineStations.contains(신도림역)).isTrue(),
                () -> assertThat(lineStations.contains(new Station("신림역"))).isFalse()
        );
    }

    @Test
    @DisplayName("전체 구간 길이를 조회한다.")
    void 전체_구간_길이_조회() {
        assertThat(lineStations.getTotalDistance()).isEqualTo(구간1.getDistance() + 구간2.getDistance());
    }
}
