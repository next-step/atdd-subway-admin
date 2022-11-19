package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    void 상행역_수정() {
        // Given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));

        Station newStation = new Station("양재역");
        Section newSection = new Section(upStation, newStation, new Distance(5));

        // When
        section.update(newSection);

        // Then
        assertThat(section.findStations()).contains(newStation);
    }

    @Test
    void 하행역_수정() {
        // Given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));

        Station newStation = new Station("양재역");
        Section newSection = new Section(newStation, downStation, new Distance(5));

        // When
        section.update(newSection);

        // Then
        assertThat(section.getDownStation()).isEqualTo(newStation);
    }

    @Test
    void 두_구간_합치기() {
        // Given
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));

        Station newStation = new Station("양재역");
        Section newSection = new Section(downStation, newStation, new Distance(5));

        // When
        Section result = section.merge(newSection);

        // Then
        assertThat(result.getDownStation()).isEqualTo(newStation);
    }
}
