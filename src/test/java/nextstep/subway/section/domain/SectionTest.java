package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SectionTest {
    public static final Station 강남역 = new Station(1L, "강남역");
    public static final Station 역삼역 = new Station(2L, "역삼역");
    public static final Station 선릉역 = new Station(3L, "선릉역");
    public static final Distance distance = Distance.from(10);

    @DisplayName("지하철 구간 생성")
    @Test
    void test_create() {
        // given & when
        Section newSection = new Section(강남역, 역삼역, distance);
        // then
        assertThat(newSection).isNotNull();
    }

    @DisplayName("상행역은 null 일 수 없다.")
    @Test
    void test_up_station_not_null() {
        assertThatThrownBy(() -> new Section(null, 역삼역, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("하행역은 null 일 수 없다.")
    @Test
    void test_down_station_not_null() {
        assertThatThrownBy(() -> new Section(강남역, null, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상/하행역은 동일할 수 없다.")
    @Test
    void test_up_down_station_not_equals() {
        assertThatThrownBy(() -> new Section(강남역, 강남역, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 길이는 null 일 수 없다.")
    @Test
    void test_distance_not_null() {
        assertThatThrownBy(() -> new Section(강남역, 역삼역, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}