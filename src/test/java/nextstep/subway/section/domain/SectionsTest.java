package nextstep.subway.section.domain;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SectionsTest {

    @ParameterizedTest
    @MethodSource("getSectionsTestParameter")
    void 역_리스트_정렬(Station A, Station B, Station C) {
        // given
        final Sections sections = new Sections();
        Section section1 = new Section(A, C, 10);
        Section section2 = new Section(A, B, 4);
        sections.add(section1);
        sections.add(section2);

        // when
        List<Station> stations = sections.getStationsInOrder();

        // then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(section1.getDistance()).isEqualTo(new Distance(6)),
                () -> assertThat(section1.getUpStation()).isEqualTo(B),
                () -> assertThat(toStationNames(stations)).containsExactly(A.getName(), B.getName(), C.getName())
        );
    }

    private static Stream<Arguments> getSectionsTestParameter() {
        Station 강남역 = new Station("강남역");
        Station 광교역 = new Station("광교역");
        Station 양재역 = new Station("양재역");

        return Stream.of(
                arguments(강남역, 광교역, 양재역),
                arguments(양재역, 광교역, 강남역),
                arguments(광교역, 양재역, 강남역)
        );
    }

    private List<String> toStationNames(List<Station> stations) {
        return stations
                .stream()
                .map(Station::getName)
                .collect(toList());
    }

}