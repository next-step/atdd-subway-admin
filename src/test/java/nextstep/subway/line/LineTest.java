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
    void toStationTest(Station[] stationArray, int[] orders) {

        Line line = new Line("line", "color");

        Section[] sections = {
            new Section(stationArray[0], stationArray[1], 100),
            new Section(stationArray[1], stationArray[2], 100),
            new Section(stationArray[2], stationArray[3], 100)
        };

        for (int order : orders) {
            line.addSection(sections[order]);
        }

        List<Station> stations = line.toStations();

        assertThat(stations).hasSize(4)
                            .containsExactly(stationArray);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> toStationTestCase() {
        return Stream.of(
            Arguments.of(new Station[] {
                new Station(1L, DONT_CARE),
                new Station(2L, DONT_CARE),
                new Station(3L, DONT_CARE),
                new Station(4L, DONT_CARE)
            }, new int[] { 0, 1, 2 }),
            Arguments.of(new Station[] {
                new Station(4L, DONT_CARE),
                new Station(3L, DONT_CARE),
                new Station(2L, DONT_CARE),
                new Station(1L, DONT_CARE)
            }, new int[] { 1, 2, 0 })
        );
    }
}
