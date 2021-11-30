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
        Sections 구간 = new Sections();
        Section 역 = new Section();

        // when
        구간.add(역);

        // then
        assertThat(구간.getSections().get(0)).isEqualTo(역);
    }

    @Test
    @DisplayName("역 사이에 노선을 등록한다.(같은 상행역)")
    void add_same_up_station() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_거리 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_거리);

        Station 양재역 = new Station("양재역");
        int 강남_양재_거리 = 3;
        Section 강남_양재_구간 = new Section(강남역, 양재역, 강남_양재_거리);

        // when
        강남_판교_구간.add(강남_양재_구간);

        // then
        List<Section> 강남_판교_구간_목록 = 강남_판교_구간.getSections();
        checkResult(강남_판교_구간_목록, 양재역, 판교역, 강남_판교_거리 - 강남_양재_거리, 강남역, 양재역, 강남_양재_거리);
    }

    @Test
    @DisplayName("역 사이에 노선을 등록한다.(같은 하행역)")
    void add_same_down_station() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_길이 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_길이);

        Station 양재역 = new Station("양재역");
        int 양재_판교_길이 = 3;

        Section 양재_판교_구간 = new Section(양재역, 판교역, 양재_판교_길이);

        // when
        강남_판교_구간.add(양재_판교_구간);

        // then
        List<Section> 강남_판교_구간_목록 = 강남_판교_구간.getSections();
        checkResult(강남_판교_구간_목록, 강남역, 양재역, 강남_판교_길이 - 양재_판교_길이, 양재역, 판교역, 양재_판교_길이);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    void add_up_station() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_길이 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_길이);

        Station 양재역 = new Station("양재역");
        int 양재_강남_길이 = 3;

        Section 양재_강남_구간 = new Section(양재역, 강남역, 양재_강남_길이);

        // when
        강남_판교_구간.add(양재_강남_구간);

        // then
        List<Section> 강남_판교_구간_목록 = 강남_판교_구간.getSections();
        checkResult(강남_판교_구간_목록, 강남역, 판교역, 강남_판교_길이, 양재역, 강남역, 양재_강남_길이);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    void add_down_station() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_길이 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_길이);

        Station 양재역 = new Station("양재역");
        int 판교_양재_길이 = 3;

        Section 판교_양재_구간 = new Section(판교역, 양재역, 판교_양재_길이, null);

        // when
        강남_판교_구간.add(판교_양재_구간);

        // then
        List<Section> 강남_판교_구간_목록 = 강남_판교_구간.getSections();
        checkResult(강남_판교_구간_목록, 강남역, 판교역, 강남_판교_길이, 판교역, 양재역, 판교_양재_길이);
    }

    @Test
    @DisplayName("이미 등록되어 있는 노선일 경우 실패한다.")
    void add_duplicate() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_길이 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_길이);

        Section 강남_판교_구간_중복 = new Section(강남역, 판교역, 강남_판교_길이);

        // when, then
        assertThatThrownBy(() -> 강남_판교_구간.add(강남_판교_구간_중복))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록되어 있는 노선입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않는 경우 실패한다.")
    void add_not_contains() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        int 강남_판교_길이 = 10;
        Sections 강남_판교_구간 = createSections(강남역, 판교역, 강남_판교_길이);

        // when, then
        Section 금정_사당_구간 = new Section(new Station("금정역"), new Station("사당역"), 강남_판교_길이);
        assertThatThrownBy(() -> 강남_판교_구간.add(금정_사당_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
    }

    @Test
    @DisplayName("지하철 역들을 상행 -> 하행 순으로 정렬하여 리턴한다.")
    void orderedStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 판교역 = new Station("판교역");
        Section 양재_판교_구간 = new Section(양재역, 판교역, 7);
        Section 강남_양재_구간 = new Section(강남역, 양재역, 3);

        Sections 강남_판교_구간 = Sections.of(양재_판교_구간, 강남_양재_구간);

        // when
        List<Station> 강남_판교_구간_역_목록 = 강남_판교_구간.orderedStations();

        // then
        List<String> 강남_판교_구간_역_이름_목록 = createStationNames(강남_판교_구간_역_목록);
        assertThat(강남_판교_구간_역_이름_목록).containsExactly("강남역", "양재역", "판교역");
    }

    @Test
    @DisplayName("상행 종점역을 제거한다.")
    void remove_상행_종점() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 판교역 = new Station("판교역");

        Section 양재_판교_구간 = new Section(양재역, 판교역, 7);
        Section 강남_양재_구간 = new Section(강남역, 양재역, 3);

        Sections 강남_판교_구간 = Sections.of(양재_판교_구간, 강남_양재_구간);

        // when
        강남_판교_구간.remove(강남역);

        // then
        assertThat(강남_판교_구간.getSections().size()).isEqualTo(1);
        assertThat(강남_판교_구간.getSections().get(0)).isEqualTo(양재_판교_구간);
    }

    @Test
    @DisplayName("하행 종점역을 제거한다.")
    void remove_하행_종점() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 판교역 = new Station("판교역");
        Section 양재_판교_구간 = new Section(양재역, 판교역, 7);
        Section 강남_양재_구간 = new Section(강남역, 양재역, 3);

        Sections 강남_판교_구간 = Sections.of(양재_판교_구간, 강남_양재_구간);

        // when
        강남_판교_구간.remove(판교역);

        // then
        assertThat(강남_판교_구간.getSections().size()).isEqualTo(1);
        assertThat(강남_판교_구간.getSections().get(0)).isEqualTo(강남_양재_구간);
    }

    @Test
    @DisplayName("중간역을 제거한다.")
    void remove_중간역() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 판교역 = new Station("판교역");
        int 양재_판교_길이 = 7;
        Section 양재_판교_구간 = new Section(양재역, 판교역, 양재_판교_길이);
        int 강남_양재_길이 = 3;
        Section 강남_양재_구간 = new Section(강남역, 양재역, 강남_양재_길이);

        Sections 강남_판교_구간 = Sections.of(양재_판교_구간, 강남_양재_구간);

        // when
        강남_판교_구간.remove(양재역);

        // then
        assertThat(강남_판교_구간.getSections().size()).isEqualTo(1);
        assertThat(강남_판교_구간.getSections().get(0).getUpStation()).isEqualTo(강남역);
        assertThat(강남_판교_구간.getSections().get(0).getDownStation()).isEqualTo(판교역);
        assertThat(강남_판교_구간.getSections().get(0).getDistance()).isEqualTo(new Distance(양재_판교_길이 + 강남_양재_길이));
    }

    @Test
    @DisplayName("구간이 하나일 경우 제거에 실패한다.")
    void remove_구간_1개() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        int 강남_양재_길이 = 3;
        Section 강남_양재_구간 = new Section(강남역, 양재역, 강남_양재_길이, null);

        Sections sections = Sections.of(강남_양재_구간);

        // when, then
        assertThatThrownBy(() -> sections.remove(강남역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 구간이 1개라 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("구간에 존재하지 않는 역일 경우 제거에 실패한다.")
    void remove_구간에_존재하지_않는_역() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 판교역 = new Station("판교역");
        int 양재_판교_길이 = 7;
        Section 양재_판교_구간 = new Section(양재역, 판교역, 양재_판교_길이, null);
        int 강남_양재_길이 = 3;
        Section 강남_양재_구간 = new Section(강남역, 양재역, 강남_양재_길이, null);

        Sections 강남_판교_구간 = Sections.of(양재_판교_구간, 강남_양재_구간);

        // when, then
        String 금정역 = "금정역";
        assertThatThrownBy(() -> 강남_판교_구간.remove(new Station(금정역)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 노선에 존재하지 않는 지하철 역입니다. (입력값: " + 금정역 + ")");
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

    private Sections createSections(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        return Sections.of(section);
    }
}