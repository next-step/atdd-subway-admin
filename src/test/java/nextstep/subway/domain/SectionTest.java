package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("구간 내 지하철 역 포함 확인")
    @Test
    void containsStation() {
        // given
        Station stationA = new Station(1L, "A역");
        Station stationB = new Station(2L, "B역");
        Section section = new Section(10, stationA, stationB);

        // when, then
        assertThat(section.containsStation(stationA)).isTrue();
        assertThat(section.containsStation(stationB)).isTrue();
    }
}
