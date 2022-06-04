package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StationTest {
    @DisplayName("지하철역 생성")
    @Test
    void test_create() {
        // given & when
        Station newStation = new Station("강남역");
        // then
        assertThat(newStation).isNotNull();
    }

    @DisplayName("지하철역 이름은 빈값일 수 없다.")
    @Test
    void test_not_null_name() {
        assertThatThrownBy(() -> new Station(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}