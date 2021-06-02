package nextstep.subway.line;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @DisplayName("toStation 메소드는 상행-하행 순으로 정리되어야 한다.")
    @Test
    void toStationTest() {

        Line line = new Line("line", "color");

        Station[] stationArray = {
            new Station(1L, "name"),
            new Station(2L, "name"),
            new Station(3L, "name"),
            new Station(4L, "name")
        };

        Section section1 = new Section(stationArray[0], stationArray[1], 100);
        line.addSection(section1);

        Section section2 = new Section(stationArray[1], stationArray[2], 100);
        line.addSection(section2);

        Section section3 = new Section(stationArray[2], stationArray[3], 100);
        line.addSection(section3);

        List<Station> stations = line.toStations();

        assertThat(stations).hasSize(4);
        assertThat(stations).hasSameElementsAs(Arrays.asList(stationArray));
    }
}
