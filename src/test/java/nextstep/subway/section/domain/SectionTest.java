package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    @DisplayName("두 구간의 상행역이 같은 역인지 확인한다.")
    void isSameUpStation() {
        // given
        Station station = new Station("강남역");
        Section section1 = new Section(station, null, 10, null);
        Section section2 = new Section(new Station("양재역"), null, 10, null);

        // when
        boolean result1 = section1.isSameUpStation(section1);
        boolean result2 = section1.isSameUpStation(section2);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("기존 구간과 새로운 구간을 수정한다.")
    void updateSection() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        Station station3 = new Station("양재역");

        int distance = 10;
        int newDistance = 3;
        Section section = new Section(station1, station2, distance, null);
        Section newSection = new Section(station1, station3, newDistance, null);

        // when
        section.updateSection(newSection);

        // then
        assertThat(section.getDownStation()).isEqualTo(station3);
        assertThat(section.getDistance()).isEqualTo(newDistance);
        assertThat(newSection.getUpStation()).isEqualTo(station3);
        assertThat(newSection.getDownStation()).isEqualTo(station2);
        assertThat(newSection.getDistance()).isEqualTo(distance - newDistance);
    }
}
