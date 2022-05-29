package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SectionTest {
    public static final Station upStation = new Station(1L, "강남역");
    public static final Station downStation = new Station(2L, "역삼역");
    public static final int distance = 10;

    @DisplayName("지하철 구간 생성")
    @Test
    void test_create() {
        Section newSection = new Section(upStation, downStation, distance);
        assertThat(newSection).isNotNull();
    }

    @DisplayName("상행 방향 역은 null 일 수 없다.")
    @Test
    void test_up_station_not_null() {
        assertThatThrownBy(() -> new Section(null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("하행 방향 역은 null 일 수 없다.")
    @Test
    void test_down_station_not_null() {
        assertThatThrownBy(() -> new Section(upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상/하행 방향 역은 동일할 수 없다.")
    @Test
    void test_up_down_station_not_equals() {
        assertThatThrownBy(() -> new Section(upStation, upStation, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 길이는 null 일 수 없다.")
    @Test
    void test_distance_not_null() {
        assertThatThrownBy(() -> new Section(this.upStation, this.downStation, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}