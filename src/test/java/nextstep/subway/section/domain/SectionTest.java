package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class SectionTest {
    public static final Section 강남_판교_구간 = new Section(1L, 강남역, 판교역, 5);
    public static final Section 판교_수지_구간 = new Section(2L, 판교역, 수지역, 5);
    public static final Section 수지_광교_구간 = new Section(3L, 수지역, 광교역, 5);


    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given & when & then
        assertThat(강남_판교_구간.getUpStation()).isEqualTo(강남역);
        assertThat(강남_판교_구간.getDownStation()).isEqualTo(판교역);
        assertThat(강남_판교_구간.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("하나의 역에 동일한 상행/하행선은 등록 불가능 테스트")
    void up_down_station_equals_register_exception() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 강남역, 5));
    }

    @Test
    @DisplayName("유효하지 않은 거리(0보다 작은) 등록 테스트")
    void distance_register_exception() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 판교역, -1));
    }

    @Test
    @DisplayName("구간 내 존재하는 역 반환 테스트")
    void toStation() {
        // given & when
        List<Station> stations = 강남_판교_구간.toStations();
        // then
        assertThat(stations.get(0)).isEqualTo(강남역);
        assertThat(stations.get(1)).isEqualTo(판교역);
   }
}
