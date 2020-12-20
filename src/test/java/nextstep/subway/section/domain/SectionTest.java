package nextstep.subway.section.domain;

import nextstep.subway.section.domain.exceptions.InvalidSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @DisplayName("상행역과 하행역을 같은 역으로 생성 할 수 없다.")
    @Test
    void upStationDownStationCannotSameTest() {
        Long stationId = 1L;
        Long distance = 10L;

        assertThatThrownBy(() -> new Section(stationId, stationId, distance))
                .isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("거리가 0인 구간은 생성할 수 없다.")
    @Test
    void distanceZeroDenyTest() {
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 0L;

        assertThatThrownBy(() -> new Section(upStationId, downStationId, distance))
                .isInstanceOf(InvalidSectionException.class);
    }
}
