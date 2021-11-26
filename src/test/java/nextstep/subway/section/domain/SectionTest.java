package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

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
        section.updateUpSection(newSection);

        // then
        assertThat(section.getUpStation()).isEqualTo(station3);
        assertThat(section.getDistance().getDistance()).isEqualTo(distance - newDistance);
    }
}
