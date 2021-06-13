package nextstep.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class StationTest {
    private static final Long STATION_ID = 1L;
    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station("서울역");
        ReflectionTestUtils.setField(station, "id", STATION_ID);
    }

    @Test
    void idMatch() {
        Long givenId = STATION_ID;
        assertThat(station.isStationIdMatch(givenId)).isTrue();
    }
}
