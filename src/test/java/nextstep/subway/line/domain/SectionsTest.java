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

    @Test
    @DisplayName("상행 종점역을 제거한다.")
    void remove_상행_종점() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("판교역");
        List<Section> sectionList = new ArrayList<>();
        Section section1 = new Section(station2, station3, 7, null);
        sectionList.add(section1);
        Section section2 = new Section(station1, station2, 3, null);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        // when
        sections.remove(station1);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0)).isEqualTo(section1);
    }

    @Test
    @DisplayName("하행 종점역을 제거한다.")
    void remove_하행_종점() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("판교역");
        List<Section> sectionList = new ArrayList<>();
        Section section1 = new Section(station2, station3, 7, null);
        sectionList.add(section1);
        Section section2 = new Section(station1, station2, 3, null);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        // when
        sections.remove(station3);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0)).isEqualTo(section2);
    }

    @Test
    @DisplayName("중간역을 제거한다.")
    void remove_중간역() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("판교역");
        List<Section> sectionList = new ArrayList<>();
        int distance1 = 7;
        Section section1 = new Section(station2, station3, distance1, null);
        sectionList.add(section1);
        int distance2 = 3;
        Section section2 = new Section(station1, station2, distance2, null);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        // when
        sections.remove(station2);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(station1);
        assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(station3);
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(new Distance(distance1 + distance2));
    }

    @Test
    @DisplayName("구간이 하나일 경우 제거에 실패한다.")
    void remove_구간_1개() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        List<Section> sectionList = new ArrayList<>();
        int distance2 = 3;
        Section section2 = new Section(station1, station2, distance2, null);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        // when, then
        assertThatThrownBy(() -> sections.remove(station1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 구간이 1개라 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("구간에 존재하지 않는 역일 경우 제거에 실패한다.")
    void remove_구간에_존재하지_않는_역() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("판교역");
        List<Section> sectionList = new ArrayList<>();
        int distance1 = 7;
        Section section1 = new Section(station2, station3, distance1, null);
        sectionList.add(section1);
        int distance2 = 3;
        Section section2 = new Section(station1, station2, distance2, null);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        // when, then
        String newStation = "금정역";
        assertThatThrownBy(() -> sections.remove(new Station(newStation)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 노선에 존재하지 않는 지하철 역입니다. (입력값: " + newStation + ")");
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