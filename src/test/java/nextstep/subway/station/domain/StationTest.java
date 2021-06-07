package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {
    public static final Station 강남역 = new Station(1L, "강남역");
    public static final Station 양재역 = new Station(2L, "양재역");
    public static final Station 판교역 = new Station(3L, "판교역");
    public static final Station 수지역 = new Station(4L, "수지역");
    public static final Station 광교역 = new Station(5L, "광교역");

    @Test
    @DisplayName("ID, 역이름 비교 테스트")
    void same() {
        // given & when & then
        assertThat(강남역.isSame(강남역)).isTrue();
        assertThat(강남역.isSame(양재역)).isFalse();
    }
}
