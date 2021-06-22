package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class StationTest {
    @DisplayName("equals 테스트")
    @Test
    void equals() {
        //given
        Station station = new Station(1L, "강남역");
        //when
        Station target = new Station(1L, "강남역");
        //then
        assertThat(station.equals(target)).isTrue();
    }
}