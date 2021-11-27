package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    @Test
    @DisplayName("첫 지하철 구간을 추가한다.")
    void add_first() {
        // given
        Sections sections = new Sections();
        Section section = new Section();

        // when
        sections.add(section);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(section);
    }

    @Test
    @DisplayName("역 사이에 노선을 등록한다.(같은 상행역)")
    void add_same_up_station() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        Station newStation = new Station("양재역");
        int newDistance = 3;
        Section newSection = new Section(station1, newStation, newDistance, null);

        // when
        sections.add(newSection);

        // then
        List<Section> sectionList = sections.getSections();
        checkResult(sectionList, newStation, station2, distance - newDistance, station1, newStation, newDistance);
    }

    @Test
    @DisplayName("역 사이에 노선을 등록한다.(같은 하행역)")
    void add_same_down_station() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        Station newStation = new Station("양재역");
        int newDistance = 3;

        Section newSection = new Section(newStation, station2, newDistance, null);

        // when
        sections.add(newSection);

        // then
        List<Section> sectionList = sections.getSections();
        checkResult(sectionList, station1, newStation, distance - newDistance, newStation, station2, newDistance);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    void add_up_station() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        Station newStation = new Station("양재역");
        int newDistance = 3;

        Section newSection = new Section(newStation, station1, newDistance, null);

        // when
        sections.add(newSection);

        // then
        List<Section> sectionList = sections.getSections();
        checkResult(sectionList, station1, station2, distance, newStation, station1, newDistance);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    void add_down_station() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        Station newStation = new Station("양재역");
        int newDistance = 3;

        Section newSection = new Section(station2, newStation, newDistance, null);

        // when
        sections.add(newSection);

        // then
        List<Section> sectionList = sections.getSections();
        checkResult(sectionList, station1, station2, distance, station2, newStation, newDistance);
    }

    @Test
    @DisplayName("이미 등록되어 있는 노선일 경우 실패한다.")
    void add_duplicate() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        Section section = new Section(station1, station2, distance, null);

        // when, then
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록되어 있는 노선입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않는 경우 실패한다.")
    void add_not_contains() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("판교역");
        int distance = 10;
        Sections sections = createSections(station1, station2, distance);

        // when, then
        Section section = new Section(new Station("금정역"), new Station("사당역"), distance, null);
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
    }

    @Test
    @DisplayName("지하철 역들을 상행 -> 하행 순으로 정렬하여 리턴한다.")
    void orderedStations() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("판교역");
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(station2, station3, 7, null));
        sectionList.add(new Section(station1, station2, 3, null));

        Sections sections = new Sections(sectionList);

        // when
        List<Station> stations = sections.orderedStations();

        // then
        List<String> names = createStationNames(stations);
        assertThat(names).containsExactly("강남역", "양재역", "판교역");
    }

    private List<String> createStationNames(List<Station> stations) {
        List<String> names = stations.stream()
                .map(s -> s.getName())
                .collect(Collectors.toList());
        return names;
    }

    private void checkResult(List<Section> sectionList,
                             Station upStation1, Station downStation1, int distance1,
                             Station upStation2, Station downStation2, int distance2) {
        assertThat(sectionList.get(0).getUpStation()).isEqualTo(upStation1);
        assertThat(sectionList.get(0).getDownStation()).isEqualTo(downStation1);
        assertThat(sectionList.get(0).getDistance().getDistance()).isEqualTo(distance1);
        assertThat(sectionList.get(1).getUpStation()).isEqualTo(upStation2);
        assertThat(sectionList.get(1).getDownStation()).isEqualTo(downStation2);
        assertThat(sectionList.get(1).getDistance().getDistance()).isEqualTo(distance2);
    }

    private Sections createSections(Station station1, Station station2, int distance) {
        Sections sections = new Sections();

        Section section = new Section(station1, station2, distance, null);

        sections.add(section);
        return sections;
    }
}