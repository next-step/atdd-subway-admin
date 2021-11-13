package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.InvalidDataException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("구간들")
class SectionsTest {

    private Sections gyodaeYeoksamSections;

    @BeforeEach
    void setUp() {
        gyodaeYeoksamSections = Sections.from(
            section("교대", "역삼", Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Sections.from(mock(Section.class)));
    }

    @Test
    @DisplayName("구간이 null인 상태로 객체화하면 IllegalArgumentException")
    void instance_nullSection_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Sections.from(null))
            .withMessage("section must not be null");
    }


    @Test
    @DisplayName("지하철 역들")
    void stations() {
        //when
        List<Station> stations = gyodaeYeoksamSections.stations();

        //then
        doesNotHaveDuplicates(stations, 2, "교대", "역삼");
    }

    @ParameterizedTest(name = "[{index}] {0} 구간을 추가하면 {1} 역들이 된다.")
    @DisplayName("구간 추가")
    @MethodSource
    void addSection(Section section, String... expectedNames) {
        //when
        gyodaeYeoksamSections.addSection(section);

        //then
        doesNotHaveDuplicates(gyodaeYeoksamSections.stations(), 3, expectedNames);
    }

    @ParameterizedTest(name = "[{index}] {0}은 교대,역삼 중 반드시 한 개의 역만 포함되어 있어야 한다.")
    @DisplayName("추가되는 역이 모두 존재하거나 모두 존재하지 않으면 InvalidDataException")
    @MethodSource
    void addSection_stationAllExistOrNot_thrownInvalidDataException(Section section) {
        //when
        ThrowingCallable addSectionCall = () -> gyodaeYeoksamSections.addSection(section);

        //then
        assertThatExceptionOfType(InvalidDataException.class)
            .isThrownBy(addSectionCall)
            .withMessageEndingWith("must be only one overlapping station");
    }

    @ParameterizedTest(name = "[{index}] {0} 구간의 거리는 기존의 교대 역삼 거리보다 크거나 같기 때문에 추가할 수 없다.")
    @DisplayName("추가되는 거리가 기존 거리보다 같거나 큰 경우 추가하면 InvalidDataException")
    @MethodSource
    void addSection_greaterThanBetweenDistance_thrownInvalidDataException(Section section) {
        //when
        ThrowingCallable addSectionCall = () -> gyodaeYeoksamSections.addSection(section);

        //then
        assertThatExceptionOfType(InvalidDataException.class)
            .isThrownBy(addSectionCall)
            .withMessageContaining("must be less than");
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("구간들의 역 삭제")
    class StationDeleteTest {

        private Sections gyodaeGangnamYeoksamSections;

        @BeforeEach
        void setUp() {
            gyodaeYeoksamSections.addSection(
                section("강남", "역삼", 10));
            gyodaeGangnamYeoksamSections = gyodaeYeoksamSections;
        }

        @ParameterizedTest(name = "[{index}] {0} 역을 삭제하면 {1} 역들이 된다.")
        @DisplayName("역 삭제")
        @MethodSource("deleteSection")
        void deleteStation(Station station, String... expectedNames) {
            //when
            gyodaeGangnamYeoksamSections.deleteStation(station);

            //then
            doesNotHaveDuplicates(gyodaeGangnamYeoksamSections.stations(), 2, expectedNames);
        }

        @Test
        @DisplayName("null 로 제공된 역 삭제")
        void deleteStation() {
            //given, when
            ThrowingCallable deleteStationCall =
                () -> gyodaeGangnamYeoksamSections.deleteStation(null);

            //then
            assertThatIllegalArgumentException()
                .isThrownBy(deleteStationCall)
                .withMessage("deleted station must not null");
        }

        @Test
        @DisplayName("존재하지 않는 역 삭제")
        void deleteStation_notStation_thrownNotFoundException() {
            Station banpo = station("반포");

            //when
            ThrowingCallable deleteStationCall =
                () -> gyodaeGangnamYeoksamSections.deleteStation(banpo);

            //then
            assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(deleteStationCall)
                .withMessageEndingWith("is not exist");
        }

        @Test
        @DisplayName("존재하지 않는 역 삭제")
        void deleteStation_remainedLastSection_thrownInvalidDataException() {
            gyodaeGangnamYeoksamSections.deleteStation(station("강남"));

            //when
            ThrowingCallable deleteStationCall =
                () -> gyodaeGangnamYeoksamSections.deleteStation(station("교대"));

            //then
            assertThatExceptionOfType(InvalidDataException.class)
                .isThrownBy(deleteStationCall)
                .withMessageEndingWith("sections that has only one section can not delete station");
        }

        @Test
        @DisplayName("존재하지 않는 역 삭제")
        void deleteStation_notStation(Station station, String... expectedNames) {
            //when
            gyodaeGangnamYeoksamSections.deleteStation(station);

            //then
            doesNotHaveDuplicates(gyodaeGangnamYeoksamSections.stations(), 2, expectedNames);
        }

        private Stream<Arguments> deleteSection() {
            return Stream.of(
                Arguments.of(station("교대"), new String[]{"강남", "역삼"}),
                Arguments.of(station("강남"), new String[]{"교대", "역삼"}),
                Arguments.of(station("역삼"), new String[]{"교대", "강남"})
            );
        }

    }

    private static Stream<Arguments> addSection() {
        return Stream.of(
            Arguments.of(
                section("강남", "역삼", 1),
                new String[]{"교대", "강남", "역삼"}
            ),
            Arguments.of(
                section("교대", "강남", 1),
                new String[]{"교대", "강남", "역삼"}
            ),
            Arguments.of(
                section("서초", "교대", Integer.MAX_VALUE),
                new String[]{"서초", "교대", "역삼"}
            ),
            Arguments.of(
                section("역삼", "선릉", Integer.MAX_VALUE),
                new String[]{"교대", "역삼", "선릉"}
            )
        );
    }

    private static Stream<Arguments> addSection_stationAllExistOrNot_thrownInvalidDataException() {
        return Stream.of(
            Arguments.of(section("반포", "논현", Integer.MAX_VALUE)),
            Arguments.of(section("교대", "역삼", Integer.MAX_VALUE))
        );
    }

    private static Stream<Arguments> addSection_greaterThanBetweenDistance_thrownInvalidDataException() {
        return Stream.of(
            Arguments.of(section("강남", "역삼", Integer.MAX_VALUE)),
            Arguments.of(section("교대", "강남", Integer.MAX_VALUE))
        );
    }

    private static Section section(String upStation, String downStation, int distance) {
        return Section.of(station(upStation),
            station(downStation),
            Distance.from(distance)
        );
    }

    private static Station station(String name) {
        return Station.from(Name.from(name));
    }

    private void doesNotHaveDuplicates(
        List<Station> stations, int expectedSize, String... expectedNames) {
        assertThat(stations)
            .hasSize(expectedSize)
            .doesNotHaveDuplicates()
            .containsExactly(
                Arrays.stream(expectedNames)
                    .map(SectionsTest::station)
                    .toArray(Station[]::new)
            );
    }


}
