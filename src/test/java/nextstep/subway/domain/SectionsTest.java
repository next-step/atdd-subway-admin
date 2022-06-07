package nextstep.subway.domain;

import nextstep.subway.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Station A역;
    private Station B역;
    private Station C역;
    private Station D역;
    private Station E역;
    private Station F역;
    private List<Section> sectionList;

    @BeforeEach
    void setUp() {
        A역 = new Station(1L, "A역");
        B역 = new Station(2L, "B역");
        C역 = new Station(3L, "C역");
        D역 = new Station(4L, "D역");
        E역 = new Station(5L, "E역");
        F역 = new Station(6L, "F역");

        sectionList = new ArrayList<>();
        sectionList.add(new Section(10, A역, B역));
        sectionList.add(new Section(10, B역, C역));
        sectionList.add(new Section(10, C역, D역));
    }

    @DisplayName("구간 리스트 내 지하철 역 포함")
    @Test
    void containStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when, then
        assertThat(sections.containStation(A역)).isTrue();
        assertThat(sections.containStation(D역)).isTrue();
    }

    @DisplayName("구간 리스트 내 지하철 역 모두 포함")
    @Test
    void containBothStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when, then
        assertThat(sections.containBothStation(new Section(30, A역, D역))).isTrue();
        assertThat(sections.containBothStation(new Section(20, B역, C역))).isTrue();
    }

    @DisplayName("구간 리스트 내 지하철 역 모두 포함 안함")
    @Test
    void containNoneStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when, then
        assertThat(sections.containNoneStation(new Section(30, E역, F역))).isTrue();
    }

    @DisplayName("상행역으로 구간 정보 조회")
    @Test
    void findSectionWithUpStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        Section section = sections.findSectionWithUpStation(B역).get();

        // then
        assertThat(section.getUpStation()).isEqualTo(B역);
        assertThat(section.getDownStation()).isEqualTo(C역);
    }

    @DisplayName("하행역으로 구간 정보 조회")
    @Test
    void findSectionWithDownStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        Section section = sections.findSectionWithDownStation(D역).get();

        // then
        assertThat(section.getUpStation()).isEqualTo(C역);
        assertThat(section.getDownStation()).isEqualTo(D역);
    }

    @DisplayName("상행종점역을 포함한 구간 정보 조회")
    @Test
    void getLineUpSection() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        Section section = sections.getLineUpSection();

        // then
        assertThat(section.getUpStation()).isEqualTo(A역);
        assertThat(section.getDownStation()).isEqualTo(B역);
    }

    @DisplayName("하행종점역 포함한 구간 정보 조회")
    @Test
    void getLineDownSection() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        Section section = sections.getLineDownSection();

        // then
        assertThat(section.getUpStation()).isEqualTo(C역);
        assertThat(section.getDownStation()).isEqualTo(D역);
    }

    @DisplayName("상행 종점역 확인")
    @Test
    void isLineUpStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when then
        assertThat(sections.isLineUpStation(A역)).isTrue();
    }

    @DisplayName("하행 종점역 확인")
    @Test
    void isLineDownStation() {
        // given
        Sections sections = new Sections(sectionList);

        // when then
        assertThat(sections.isLineDownStation(D역)).isTrue();
    }

    @DisplayName("상행 종점역 추가")
    @Test
    void insertSectionWhenSectionIsHead() {
        // given
        Sections sections = new Sections(sectionList);
        Section section = new Section(10, E역, A역);

        // when
        sections.insertSectionWhenSectionIsHead(new Line(), section);

        // that
        assertThat(sections.getLineUpStation()).isEqualTo(E역);
    }

    @DisplayName("하행 종점역 추가")
    @Test
    void insertSectionWhenSectionIsTail() {
        // given
        Sections sections = new Sections(sectionList);
        Section section = new Section(10, D역, F역);

        // when
        sections.insertSectionWhenSectionIsTail(new Line(), section);

        // that
        assertThat(sections.getLineDownStation()).isEqualTo(F역);
    }

    @DisplayName("상행역이 포함된 구간 추가")
    @Test
    void insertSectionWhenStationIsIncluded_1() {
        // given
        Sections sections = new Sections(sectionList);
        Section section = new Section(5, A역, F역);

        // when
        sections.insertSectionWhenStationIsIncluded(null, section);

        // then
        Section find = sections.findSectionWithUpStation(A역).get();
        assertThat(find.getDownStation()).isEqualTo(F역);
    }

    @DisplayName("하행역 포함된 구간 추가")
    @Test
    void insertSectionWhenStationIsIncluded_2() {
        // given
        Sections sections = new Sections(sectionList);
        Section section = new Section(5, F역, D역);

        // when
        sections.insertSectionWhenStationIsIncluded(null, section);

        // then
        Section find = sections.findSectionWithDownStation(D역).get();
        assertThat(find.getUpStation()).isEqualTo(F역);
    }

    @DisplayName("자유로운 구간 추가")
    @Test
    void insertSection() {
        // given
        Sections sections = new Sections(sectionList);
        Station X역 = new Station(51L, "X역");
        Station Y역 = new Station(52L, "Y역");
        Station Z역 = new Station(53L, "Z역");

        // when
        sections.insertSection(null, new Section(15, X역, A역));
        sections.insertSection(null, new Section(7, Y역, D역));
        sections.insertSection(null, new Section(10, D역, Z역));

        // then
        assertThat(sections.getLineUpStation()).isEqualTo(X역);
        assertThat(sections.getLineDownStation()).isEqualTo(Z역);
    }

    @DisplayName("정렬된 구간 정보 가져오기")
    @Test
    void getSortedSections() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        List<Section> list = sections.getSortedSections().getList();

        // then
        assertThat(list.get(0).getUpStation()).isEqualTo(A역);
        assertThat(list.get(0).getDownStation()).isEqualTo(B역);
        assertThat(list.get(list.size() - 1).getUpStation()).isEqualTo(C역);
        assertThat(list.get(list.size() - 1).getDownStation()).isEqualTo(D역);
    }

    @DisplayName("정렬된 지하철역 정보 가져오기")
    @Test
    void getSortedLineStations() {
        // given
        Sections sections = new Sections(sectionList);

        // when
        List<Station> list = sections.getSortedLineStations();

        // then
        assertThat(list.get(0)).isEqualTo(A역);
        assertThat(list.get(list.size() - 1)).isEqualTo(D역);
    }
}
