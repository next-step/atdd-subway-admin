package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    @DisplayName("upStation과 downStation이 같은 경우 예외발생")
    @Test
    void checkDuplicateStation() {
        //Given
        Station station = new Station("강남역");

        //When+Then
        assertThatThrownBy(() -> new Section(station, station, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Section이 Line에 저장되었는지 확인")
    @Test
    void checkSectionisAddedToLine() {
        //Given
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        Section section = new Section(upStation, downStation, 10);

        //When
        Line line = new Line("신분당선", "red");
        section.ofLine(line);

        //Then
        assertThat(line.getSections().contains(section)).isTrue();
    }
}
