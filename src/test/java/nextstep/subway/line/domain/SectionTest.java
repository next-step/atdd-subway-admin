package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.InvalidDataException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
                station("강남"), station("역삼"),
                Distance.from(Integer.MAX_VALUE)));
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
    @DisplayName("같은 역으로 객체화하면 IllegalArgumentException")
    void instance_sameUpAndDownStation_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Section.of(station("강남"), station("강남"),
                Distance.from(Integer.MAX_VALUE)))
            .withMessageEndingWith(" must not equal");
    }

    @Test
    @DisplayName("지하철 역들")
    void stations() {
        String gangnam = "강남";
        String yeoksam = "역삼";
        Section section = Section.of(
            station("강남"), station("역삼"), Distance.from(Integer.MAX_VALUE));

        //when
        List<Station> stations = section.stations();

        //then
        hasTwoSizeAndContains(stations, gangnam, yeoksam);
    }

    @ParameterizedTest(name = "[{index}] {0} 구간을 제거하면 {1}")
    @MethodSource
    @DisplayName("구간 제거")
    void remove(Section removedSection, Section expected) {
        Section section = Section.of(
            station("교대"), station("역삼"), Distance.from(5));

        //when
        section.remove(removedSection);

        //then
        assertThat(section)
            .isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] 교대,역삼 구간에서 {0} 구간을 제거할 수 없다")
    @MethodSource
    @DisplayName("모든 역이 같거나 다른 구간을 제거하면 IllegalArgumentException")
    void remove_sameOrNotExistStation_thrownIllegalArgumentException(Section removedSection) {
        Section section = Section.of(
            station("교대"), station("역삼"), Distance.from(Integer.MAX_VALUE));

        //when
        ThrowingCallable removeCall = () -> section.remove(removedSection);

        //then
        assertThatExceptionOfType(InvalidDataException.class)
            .isThrownBy(removeCall)
            .withMessageContaining("can not be removed from");
    }

    @Test
    @DisplayName("제거되는 구간의 길이가 더 크거나 같은 상태로 제거하면 IllegalArgumentException")
    void remove_greaterDistance_thrownIllegalArgumentException() {
        Section section = Section.of(
            station("교대"), station("역삼"), Distance.from(Integer.MAX_VALUE));

        //when
        ThrowingCallable removeCall = () -> section.remove(
            Section.of(station("교대"), station("강남"), Distance.from(Integer.MAX_VALUE))
        );

        //then
        assertThatExceptionOfType(InvalidDataException.class)
            .isThrownBy(removeCall)
            .withMessageContaining("must be less than");
    }

    private static Station station(String name) {
        return Station.from(Name.from(name));
    }

    private static Stream<Arguments> instance_emptyArgument_thrownIllegalArgumentException() {
        return Stream.of(
            Arguments.of(null, station("역삼"), Distance.from(10)),
            Arguments.of(station("강남"), null, Distance.from(10)),
            Arguments.of(station("강남"), station("역삼"), null)
        );
    }

    private static Stream<Arguments> remove() {
        return Stream.of(
            Arguments.of(
                Section.of(station("교대"), station("강남"), Distance.from(3)),
                Section.of(station("강남"), station("역삼"), Distance.from(2))
            ),
            Arguments.of(
                Section.of(station("강남"), station("역삼"), Distance.from(2)),
                Section.of(station("교대"), station("강남"), Distance.from(3))
            )
        );
    }

    private static Stream<Arguments> remove_sameOrNotExistStation_thrownIllegalArgumentException() {
        return Stream.of(
            Arguments.of(Section.of(station("반포"), station("논현"), Distance.from(3))),
            Arguments.of(Section.of(station("교대"), station("역삼"), Distance.from(3)))
        );
    }

    private void hasTwoSizeAndContains(List<Station> stations, String firstExpectedName,
        String secondExpectedName) {
        assertThat(stations)
            .hasSize(2)
            .doesNotHaveDuplicates()
            .containsExactly(station(firstExpectedName), station(secondExpectedName));
    }
}
