package nextstep.subway.section.domain;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SectionsTest {

    private static final Station 강남역 = new Station("강남역");
    private static final Station 광교역 = new Station("광교역");
    private static final Station 양재역 = new Station("양재역");
    private static final Station 판교역 = new Station("판교역");
    private static final Station 정자역 = new Station("정자역");

    @ParameterizedTest
    @MethodSource("역_리스트_정렬_파라미터")
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

    private static Stream<Arguments> 역_리스트_정렬_파라미터() {
        return Stream.of(
                arguments(강남역, 광교역, 양재역),
                arguments(양재역, 광교역, 강남역),
                arguments(광교역, 양재역, 강남역)
        );
    }

    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        // given
        final Sections sections = new Sections();
        Section section1 = new Section(강남역, 광교역, 7);
        Section section2 = new Section(양재역, 강남역, 4);
        sections.add(section1);

        // when
        sections.add(section2);
        List<Station> stations = sections.getStationsInOrder();

        // then
        assertAll(
                () -> assertThat(toStationNames(stations)).containsExactly(양재역.getName(), 강남역.getName(), 광교역.getName()),
                () -> assertThat(section1.getDistance()).isEqualTo(new Distance(7)),
                () -> assertThat(section2.getDistance()).isEqualTo(new Distance(4))
        );
    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {
        // given
        final Sections sections = new Sections();
        Section section1 = new Section(강남역, 광교역, 7);
        Section section2 = new Section(광교역, 양재역, 4);
        sections.add(section1);

        // when
        sections.add(section2);
        List<Station> stations = sections.getStationsInOrder();

        // then
        assertAll(
                () -> assertThat(toStationNames(stations)).containsExactly(강남역.getName(), 광교역.getName(), 양재역.getName()),
                () -> assertThat(section1.getDistance()).isEqualTo(new Distance(7)),
                () -> assertThat(section2.getDistance()).isEqualTo(new Distance(4))
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 구간_등록_시_예외_케이스() {
        // given
        final Sections sections = new Sections();
        Section section1 = new Section(강남역, 광교역, 10);
        sections.add(section1);

        // when & then
        Section section2 = new Section(강남역, 양재역, 10);
        assertThatThrownBy(() -> sections.add(section2)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 구간_등록_시_예외_케이스_2() {
        // given
        final Sections sections = new Sections();
        Section section1 = new Section(강남역, 광교역, 7);
        Section section2 = new Section(양재역, 광교역, 5);
        sections.add(section1);
        sections.add(section2);

        // when & then
        Section section3 = new Section(판교역, 정자역, 4);
        assertThatThrownBy(() -> sections.add(section3)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @ParameterizedTest
    @MethodSource("구간_등록_시_예외_케이스_3_파라미터")
    void 구간_등록_시_예외_케이스_3(Section section1, Section section2, Section section3) {
        // given
        final Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        // when & then
        assertThatThrownBy(() -> sections.add(section3)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> 구간_등록_시_예외_케이스_3_파라미터() {
        return Stream.of(
                arguments(new Section(강남역, 양재역, 7), new Section(양재역, 광교역, 5), new Section(강남역, 광교역, 7)),
                arguments(new Section(강남역, 양재역, 7), new Section(강남역, 광교역, 5), new Section(강남역, 광교역, 4)),
                arguments(new Section(강남역, 양재역, 7), new Section(광교역, 양재역, 5), new Section(강남역, 양재역, 2))
        );
    }

    @DisplayName("연결할 수 있는 최초 상행역이 없음")
    @Test
    void 구간_등록_시_예외_케이스_4() {
        // given
        final Sections sections = new Sections();

        // when & then
        assertThatThrownBy(() -> sections.getStationsInOrder()).isInstanceOf(IllegalArgumentException.class);
    }

    private List<String> toStationNames(List<Station> stations) {
        return stations
                .stream()
                .map(Station::getName)
                .collect(toList());
    }

}