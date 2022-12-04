package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.AllRegisteredStationsException;
import nextstep.subway.exception.LastSectionException;
import nextstep.subway.exception.NotAllIncludedStationsException;

class SectionsTest {

    private Line line;
    private Station 교대역;
    private Station 역삼역;
    private Station 강남역;

    @BeforeEach
    void init() {
        // this.line = Line.of("2노선", "green", 5, upStation, downStation);
        this.교대역 = new Station(1L, "교대역");
        this.역삼역 = new Station(2L, "역삼역");
        this.강남역 = new Station(3L, "강남역");
    }

    @DisplayName("구간이 0개 일 때 빈 리스트 반환")
    @Test
    void getStations1() {
        Sections sections = new Sections();

        List<Section> findSections = sections.getStationsInOrder();

        assertThat(findSections).hasSize(0);
    }

    @DisplayName("구간이 있을 때 결과 반환")
    @Test
    void getStations2() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        List<Section> findSections = sections.getStationsInOrder();

        assertThat(findSections).hasSize(2);
        Section firstSection = findSections.get(0);
        Section secondSection = findSections.get(1);
        assertThat(firstSection.getUpStation()).isEqualTo(null);
        assertThat(firstSection.getDownStation().getName()).isEqualTo("교대역");
        assertThat(secondSection.getUpStation().getId()).isEqualTo(1L);
        assertThat(secondSection.getDownStation().getName()).isEqualTo("역삼역");
    }

    @DisplayName("구간 중간에 새로운 구간 추가")
    @Test
    void addSection1() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        sections.add(new Section(강남역, 교대역, 3));

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(3);
        assertThat(
            findSections.stream().map(section -> section.getDownStation().getName()).collect(Collectors.toList()))
            .containsExactly("교대역", "강남역", "역삼역");
        assertThat(findSections.get(1).getDistance()).isEqualTo(3);
        assertThat(findSections.get(2).getDistance()).isEqualTo(2);
    }

    @DisplayName("신규 구간의 역이 이미 구간들에 모두 등록된 역일 때")
    @Test
    void addSection2() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        assertThatThrownBy(() -> sections.add(new Section(역삼역, 교대역, 3)))
            .isInstanceOf(AllRegisteredStationsException.class)
            .hasMessage("이미 상행역/하행역 모두 노선에 추가되어 있습니다.");
    }

    @DisplayName("신규 구간의 역 모두 기존 구간에 존재하지 않는 역일 때")
    @Test
    void addSection3() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        assertThatThrownBy(() -> sections.add(new Section(강남역, new Station(99L), 3)))
            .isInstanceOf(NotAllIncludedStationsException.class)
            .hasMessage("상행역/하행역 모두 노선에 추가되어있지 않습니다.");
    }

    @DisplayName("하행역이 일치하고 상행역이 추가되는 구간 추가")
    @Test
    void addSection5() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        sections.add(new Section(역삼역, 강남역, 3));

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(3);
        assertThat(
            findSections.stream().map(section -> section.getDownStation().getName()).collect(Collectors.toList()))
            .containsExactly("교대역", "강남역", "역삼역");
        assertThat(findSections.get(1).getDistance()).isEqualTo(2);
        assertThat(findSections.get(2).getDistance()).isEqualTo(3);
    }

    @DisplayName("구간 중간의 역 삭제")
    @Test
    void removeSection1() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);
        sections.add(new Section(강남역, 교대역, 3));

        sections.deleteSection(강남역.getId());

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(2);
        assertThat(
            findSections.stream().map(section -> section.getDownStation().getName()).collect(Collectors.toList()))
            .containsExactly("교대역", "역삼역");
        assertThat(findSections.get(1).getDistance()).isEqualTo(5);
    }

    @DisplayName("구간의 최상위역 삭제")
    @Test
    void removeSection2() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);
        sections.add(new Section(강남역, 교대역, 3));

        sections.deleteSection(교대역.getId());

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(2);
        assertThat(
            findSections.stream().map(section -> section.getDownStation().getName()).collect(Collectors.toList()))
            .containsExactly("강남역", "역삼역");
        assertThat(findSections.get(1).getDistance()).isEqualTo(2);
    }

    @DisplayName("구간의 최하위역 삭제")
    @Test
    void removeSection3() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);
        sections.add(new Section(강남역, 교대역, 3));

        sections.deleteSection(역삼역.getId());

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(2);
        assertThat(
            findSections.stream().map(section -> section.getDownStation().getName()).collect(Collectors.toList()))
            .containsExactly("교대역", "강남역");
        assertThat(findSections.get(1).getDistance()).isEqualTo(3);
    }

    @DisplayName("구간 전체 삭제")
    @Test
    void removeSection4() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);
        sections.add(new Section(강남역, 교대역, 3));

        sections.remove();

        List<Section> findSections = sections.getStationsInOrder();
        assertThat(findSections).hasSize(0);
    }

    @DisplayName("마지막 구간은 삭제할 수 없")
    @Test
    void removeSection5() {
        Sections sections = new Sections();
        sections.init(교대역, 역삼역, 5);

        assertThatThrownBy(() -> sections.deleteSection(역삼역.getId()))
            .isInstanceOf(LastSectionException.class)
            .hasMessage("노선의 마지막 구간 역은 지울 수 없습니다.");
    }

}
