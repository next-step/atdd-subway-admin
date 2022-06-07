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

    @DisplayName("구간 연결하기")
    @Test
    void connectSection() {
        // given
        Station stationA = new Station(1L, "A역");
        Station stationB = new Station(2L, "B역");
        Station stationC = new Station(3L, "C역");
        Section section1 = new Section(10, stationA, stationB);
        Section section2 = new Section(10, stationB, stationC);

        // when
        Section actual = section1.connectSection(section2);

        // then
        assertThat(actual.getUpStation()).isEqualTo(stationA);
        assertThat(actual.getDownStation()).isEqualTo(stationC);
        assertThat(actual.getDistance()).isEqualTo(new Distance(20));
    }
}
