package nextstep.subway.line;

import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private static final String DONT_CARE = "name";

    @DisplayName("toStation 메소드는 상행-하행 순으로 정리되어야 한다.")
    @MethodSource("toStationTestCase")
    @ParameterizedTest
    void toStationTest(Station[] stationArray) {

        Line line = new Line("line", "color");

        Section section1 = new Section(stationArray[0], stationArray[1], 100);
        line.addSection(section1);

        Section section2 = new Section(stationArray[1], stationArray[2], 100);
        line.addSection(section2);

        Section section3 = new Section(stationArray[2], stationArray[3], 100);
        line.addSection(section3);

        List<Station> stations = line.toStations();

        assertThat(stations).hasSize(4)
                            .containsExactly(stationArray);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> toStationTestCase() {
        return Stream.of(
            Arguments.of((Object) new Station[] {
                new Station(1L, DONT_CARE),
                new Station(2L, DONT_CARE),
                new Station(3L, DONT_CARE),
                new Station(4L, DONT_CARE)
            }),
            Arguments.of((Object) new Station[] {
                new Station(4L, DONT_CARE),
                new Station(3L, DONT_CARE),
                new Station(2L, DONT_CARE),
                new Station(1L, DONT_CARE)
            })
        );
    }
}
