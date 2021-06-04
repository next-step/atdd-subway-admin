package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given
        Section section = new Section(강남역, 판교역, 5);
        // when & then
        assertThat(section.getUpStation()).isEqualTo(강남역);
        assertThat(section.getDownStation()).isEqualTo(판교역);
        assertThat(section.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("동일한 상행/하행선 등록 테스트")
    void up_down_station_equals_register_exception() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 강남역, 5));
    }

    @Test
    @DisplayName("유효하지 않은 거리 등록 테스트")
    void distance_register_exception() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Section(강남역, 판교역, -1));
    }
}
