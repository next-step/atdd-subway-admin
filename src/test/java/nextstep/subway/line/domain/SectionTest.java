package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("구간")
class SectionTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Section.of(
                Station.from(Name.from("강남")), Station.from(Name.from("역삼")),
                Distance.from(10)));
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 값 으로 객체화 할 수 없다.")
    @MethodSource
    @DisplayName("'null' 인자가 존재한 상태로 객체화하면 IllegalArgumentException")
    void instance_emptyArgument_thrownIllegalArgumentException(
        Station upStation, Station downStation, Distance distance) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Section.of(upStation, downStation, distance))
            .withMessageEndingWith(" must not be null");
    }

    @Test
    @DisplayName("지하철 역들")
    void stations() {
        Name gangnam = Name.from("강남");
        Name yeoksam = Name.from("역삼");
        Section section = Section.of(
            Station.from(gangnam), Station.from(yeoksam), Distance.from(10));

        //when
        List<Station> stations = section.stations();

        //then
        hasTwoSizeAndContains(stations, gangnam, yeoksam);
    }

    private static Stream<Arguments> instance_emptyArgument_thrownIllegalArgumentException() {
        return Stream.of(
            Arguments.of(null, Station.from(Name.from("역삼")), Distance.from(10)),
            Arguments.of(Station.from(Name.from("강남")), null, Distance.from(10)),
            Arguments.of(Station.from(Name.from("강남")), Station.from(Name.from("역삼")), null)
        );
    }

    private void hasTwoSizeAndContains(List<Station> stations, Name firstExpectedName,
        Name secondExpectedName) {
        assertThat(stations)
            .hasSize(2)
            .doesNotHaveDuplicates()
            .extracting(Station::name)
            .containsExactly(firstExpectedName, secondExpectedName);
    }
}
