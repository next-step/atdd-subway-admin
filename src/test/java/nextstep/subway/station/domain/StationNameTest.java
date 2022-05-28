package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class StationNameTest {
    @DisplayName("지하철역 이름은 null 이거나 빈값일 수 없다.")
    @Test
    void test_not_null() {
        assertAll(
                () -> assertThatThrownBy(() -> StationName.from(null))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> StationName.from(""))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}