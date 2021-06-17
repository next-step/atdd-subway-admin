package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("역 사이에 새로운 역을 등록할 경우")
class SectionsTest {
    private static Station A, B, C, D, E, F;
    private static Station X, Y, Z;

    @BeforeAll
    public static void setup() {
        //given
        A = new Station(1L,"A");    B = new Station(2L,"B");    C = new Station(3L,"C");
        D = new Station(4L,"D");    E = new Station(5L,"E");    F = new Station(6L,"F");
        X = new Station(24L,"X");   Y = new Station(25L,"Y");   Z = new Station(26L,"Z");
    }

    @Test
    @DisplayName("한 구간에 시발역과 종착역")
    public void step0_1() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(B)
                .distance(2)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(2)
                .build();

        Section section3 = Section.builder().id(3L)
                .upStation(C).downStation(D)
                .distance(2)
                .build();

        assertAll(
            () -> assertThat(appendSectionAndPrint(section1).firstStation()).isEqualTo(A),
            () -> assertThat(appendSectionAndPrint(section1).lastStation()).isEqualTo(B),
            () -> assertThat(appendSectionAndPrint(section1, section2).firstStation()).isEqualTo(A),
            () -> assertThat(appendSectionAndPrint(section1, section2).lastStation()).isEqualTo(C),

            () -> assertThat(appendSectionAndPrint(section1, section2, section3).firstStation()).isEqualTo(A),
            () -> assertThat(appendSectionAndPrint(section2, section1, section3).firstStation()).isEqualTo(A),
            () -> assertThat(appendSectionAndPrint(section2, section3, section1).firstStation()).isEqualTo(A),
            () -> assertThat(appendSectionAndPrint(section3, section2, section1).firstStation()).isEqualTo(A),

            () -> assertThat(appendSectionAndPrint(section1, section2, section3).lastStation()).isEqualTo(D),
            () -> assertThat(appendSectionAndPrint(section2, section1, section3).lastStation()).isEqualTo(D),
            () -> assertThat(appendSectionAndPrint(section2, section3, section1).lastStation()).isEqualTo(D),
            () -> assertThat(appendSectionAndPrint(section3, section2, section1).lastStation()).isEqualTo(D)
        );
    }

    @Test
    @DisplayName("한 구간에 시작구간과 마지막구간")
    public void step0_2() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(B)
                .distance(2)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(2)
                .build();

        Section section3 = Section.builder().id(3L)
                .upStation(C).downStation(D)
                .distance(2)
                .build();

        assertAll(
            () -> assertThat(appendSectionAndPrint(section1).firstSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section1).lastSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section1, section2).firstSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section1, section2).lastSection()).isEqualTo(section2),

            () -> assertThat(appendSectionAndPrint(section1, section2, section3).firstSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section2, section1, section3).firstSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section2, section3, section1).firstSection()).isEqualTo(section1),
            () -> assertThat(appendSectionAndPrint(section3, section2, section1).firstSection()).isEqualTo(section1),

            () -> assertThat(appendSectionAndPrint(section1, section2, section3).lastSection()).isEqualTo(section3),
            () -> assertThat(appendSectionAndPrint(section2, section1, section3).lastSection()).isEqualTo(section3),
            () -> assertThat(appendSectionAndPrint(section2, section3, section1).lastSection()).isEqualTo(section3),
            () -> assertThat(appendSectionAndPrint(section3, section2, section1).lastSection()).isEqualTo(section3)
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우. [상행선]이 일치하는 경우")
    public void step1_1() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(A).downStation(B)
                .distance(4)
                .build();

        Sections sections = appendSectionAndPrint(section1, section2);

        List<Station> sortedStations = sections.sortedStations();
        List<Section> sortedSections = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(sortedStations.size()).isEqualTo(3),

            //순서 확인
            () -> assertThat(sortedStations).containsExactly(A, B, C),
            () -> assertThat(sections.firstStation()).isEqualTo(A),
            () -> assertThat(sections.lastStation()).isEqualTo(C),

            //길이 확인
            () -> assertThat(sortedSections.get(0).getDistance()).isEqualTo(4),
            () -> assertThat(sortedSections.get(1).getDistance()).isEqualTo(3),
            () -> assertThat(sections.totalDistance()).isEqualTo(7)
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우. [하행선]이 일치하는 경우")
    public void step1_2() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(4)
                .build();

        Sections sections = appendSectionAndPrint(section1, section2);

        List<Station> sortedStations = sections.sortedStations();
        List<Section> sortedSections = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(sortedStations.size()).isEqualTo(3),

            //순서 확인
            () -> assertThat(sortedStations).containsExactly(A, B, C),
            () -> assertThat(sections.firstStation()).isEqualTo(A),
            () -> assertThat(sections.lastStation()).isEqualTo(C),

            //길이 확인
            () -> assertThat(sortedSections.get(0).getDistance()).isEqualTo(3),
            () -> assertThat(sortedSections.get(1).getDistance()).isEqualTo(4),
            () -> assertThat(sections.totalDistance()).isEqualTo(7)
        );
    }

    @Test
    @DisplayName("새로운 역을 [상행 종점]으로 등록할 경우")
    public void step2() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(A)
                .distance(4)
                .build();

        Sections sections = appendSectionAndPrint(section1, section2);

        List<Station> sortedStations = sections.sortedStations();
        List<Section> sortedSections = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(sortedStations.size()).isEqualTo(3),

            //순서 확인
            () -> assertThat(sortedStations).containsExactly(B, A, C),
            () -> assertThat(sections.firstStation()).isEqualTo(B),
            () -> assertThat(sections.lastStation()).isEqualTo(C),

            //길이 확인
            () -> assertThat(sortedSections.get(0).getDistance()).isEqualTo(4),
            () -> assertThat(sortedSections.get(1).getDistance()).isEqualTo(7),
            () -> assertThat(sections.totalDistance()).isEqualTo(11)
        );
    }

    @Test
    @DisplayName("새로운 역을 [하행 종점]으로 등록할 경우")
    public void step3() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(C).downStation(B)
                .distance(3)
                .build();

        Sections sections = appendSectionAndPrint(section1, section2);

        List<Station> sortedStations = sections.sortedStations();
        List<Section> sortedSections = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(sortedStations.size()).isEqualTo(3),

            //순서 확인
            () -> assertThat(sortedStations).containsExactly(A, C, B),
            () -> assertThat(sections.firstStation()).isEqualTo(A),
            () -> assertThat(sections.lastStation()).isEqualTo(B),

            //길이 확인
            () -> assertThat(sortedSections.get(0).getDistance()).isEqualTo(7),
            () -> assertThat(sortedSections.get(1).getDistance()).isEqualTo(3),
            () -> assertThat(sections.totalDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    public void step4() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(D)
                .distance(10)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(A).downStation(B)
                .distance(4)
                .build();

        Section section3 = Section.builder().id(3L)
                .upStation(B).downStation(C)
                .distance(2)
                .build();

        Sections sections = appendSectionAndPrint(section1, section2, section3);

        List<Station> actual = sections.sortedStations();
        List<Section> sorted = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(actual.size()).isEqualTo(4),

            //순서 확인
            () -> assertThat(actual).containsExactly(A, B, C, D),
            () -> assertThat(sections.firstStation()).isEqualTo(A),
            () -> assertThat(sections.lastStation()).isEqualTo(D),

            //길이 확인
            () -> assertThat(sorted.get(0).getDistance()).isEqualTo(4),
            () -> assertThat(sorted.get(1).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(2).getDistance()).isEqualTo(4),
            () -> assertThat(sections.totalDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("다중 역등록 테스트")
    public void step5() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(B)
                .distance(2)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(D)
                .distance(4)
                .build();

        Section section3 = Section.builder().id(3L)
                .upStation(B).downStation(C)
                .distance(2)
                .build();

        Section section4 = Section.builder().id(4L)
                .upStation(D).downStation(F)
                .distance(4)
                .build();

        Section section5 = Section.builder().id(5L)
                .upStation(D).downStation(E)
                .distance(2)
                .build();

        Section section6 = Section.builder().id(6L)
                .upStation(F).downStation(Z)
                .distance(40)
                .build();

        Section section7 = Section.builder().id(7L)
                .upStation(X).downStation(Z)
                .distance(4)
                .build();

        Section section8 = Section.builder().id(8L)
                .upStation(Y).downStation(Z)
                .distance(2)
                .build();

        Sections sections = appendSectionAndPrint(
            section1, section2, section3, section4,
            section5, section6, section7, section8
        );

        List<Station> actual = sections.sortedStations();
        List<Section> sorted = sections.sortedSections();

        assertAll(
            //갯수 확인
            () -> assertThat(actual.size()).isEqualTo(9),

            //순서 확인
            () -> assertThat(actual).containsExactly(A, B, C, D, E, F, X, Y, Z),
            () -> assertThat(sections.firstStation()).isEqualTo(A),
            () -> assertThat(sections.lastStation()).isEqualTo(Z),

            //길이 확인
            () -> assertThat(sorted.get(0).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(1).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(2).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(3).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(4).getDistance()).isEqualTo(2),

            () -> assertThat(sorted.get(5).getDistance()).isEqualTo(36),
            () -> assertThat(sorted.get(6).getDistance()).isEqualTo(2),
            () -> assertThat(sorted.get(7).getDistance()).isEqualTo(2),
            () -> assertThat(sections.totalDistance()).isEqualTo(50)
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    public void exception1() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(7)
                .build();

        assertThatThrownBy(() -> appendSectionAndPrint(section1, section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    public void exception2() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(B)
                .distance(10)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(10)
                .build();

        Section wrongSection1 = Section.builder().id(3L)
                .upStation(B).downStation(C)
                .distance(7)
                .build();

        Section wrongSection2 = Section.builder().id(4L)
                .upStation(A).downStation(C)
                .distance(7)
                .build();

        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        System.out.println(sections.format());

        assertThatThrownBy(() -> sections.add(wrongSection1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> sections.add(wrongSection2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    public void exception3() {
        Section section1 = Section.builder().id(1L)
                .upStation(A).downStation(B)
                .distance(3)
                .build();

        Section section2 = Section.builder().id(2L)
                .upStation(B).downStation(C)
                .distance(4)
                .build();

        Section wrongSection1 = Section.builder().id(2L)
                .upStation(new Station(1000L, "X")).downStation(new Station(1001L, "Y"))
                .distance(7)
                .build();

        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        System.out.println(sections.format());

        assertThatThrownBy(() -> sections.add(wrongSection1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Sections appendSectionAndPrint(final Section ...items) {
        Sections sections = new Sections();
        for(Section section : items) {
            sections.add(section);
        }

        System.out.println(sections.format());

        return sections;
    }
}