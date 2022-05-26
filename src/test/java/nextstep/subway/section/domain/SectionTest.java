package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.exception.StationExceptionMessage.CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.DISTANCE_IS_NOT_NULL;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.DOWN_STATION_IS_NOT_NULL;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.UP_STATION_IS_NOT_NULL;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    private static final int DISTANCE = 5;

    private Station 판교역;
    private Station 강남역;
    private Distance distance;

    @BeforeEach
    void setUp() {
        판교역 = Station.of(1L, "판교역");
        강남역 = Station.of(2L, "강남역");
        distance = Distance.from(DISTANCE);
    }

    @DisplayName("지하철 구간을 상행역과 하행역, 거리를 통해 생성할 수 있다.")
    @Test
    void generate01() {
        // given & when
        Section section = Section.of(판교역, 강남역, distance);

        // then
        assertAll(
            () -> assertEquals(section.getUpStation(), 판교역),
            () -> assertEquals(section.getDownStation(), 강남역),
            () -> assertEquals(section.getDistance(), distance)
        );
    }

    @DisplayName("지하철 구간 생성 시 상행역 없이 생성될 수 없다.")
    @Test
    void generate02() {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(null, 강남역, distance))
            .withMessageContaining(UP_STATION_IS_NOT_NULL.getMessage());
    }

    @DisplayName("지하철 구간 생성 시 하행역 없이 생성될 수 없다.")
    @Test
    void generate03() {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(판교역, null, distance))
            .withMessageContaining(DOWN_STATION_IS_NOT_NULL.getMessage());
    }

    @DisplayName("지하철 구간 생성 시 구간 없이 생성될 수 없다.")
    @Test
    void generate04() {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(판교역, 강남역, null))
            .withMessageContaining(DISTANCE_IS_NOT_NULL.getMessage());
    }

    @DisplayName("지하철 구간 생성 시 상, 하행 구간이 같을 수 없다.")
    @Test
    void generate05() {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(판교역, 판교역, distance))
            .withMessageContaining(CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION.getMessage());
    }
}