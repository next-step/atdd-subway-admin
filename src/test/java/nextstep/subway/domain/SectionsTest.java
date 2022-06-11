package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class SectionsTest {

    private static final Line DEFAULT_LINE = new Line(1L, "1호선", "black-pink");
    private static final Station DEFAULT_FIRST_STATION = new Station(1L, "의정부");
    private static final Station DEFAULT_LAST_STATION = new Station(2L, "동탄");

    @DisplayName("정렬된 역 목록 조회 확인")
    @Test
    void getSortedStations() {
        Sections sections = makeSectionsForTest();

        assertThat(sections.getSortedStations()).containsExactly(DEFAULT_FIRST_STATION, DEFAULT_LAST_STATION);
    }

    @DisplayName("신규 상행 역 구간 추가 시 역 목록 첫번째 항목 확인")
    @Test
    void addSectionWithNewFirstStation() {
        Sections sections = makeSectionsForTest();

        Station newFirstStation = new Station(3L, "동두천");
        Section newFirstSection = new Section(2L, 1L, newFirstStation, DEFAULT_FIRST_STATION, 10);
        sections.addSection(newFirstSection);

        assertThat(sections.getSortedStations()).containsExactly(newFirstStation, DEFAULT_FIRST_STATION,
                DEFAULT_LAST_STATION);
    }

    @DisplayName("신규 하행 역 구간 추가 시 역 목록 마지막 항목 확인")
    @Test
    void addSectionWithNewLastStation() {
        Sections sections = makeSectionsForTest();

        Station newLastStation = new Station(4L, "천안");
        Section newLastSection = new Section(3L, 1L, DEFAULT_LAST_STATION, newLastStation, 7);
        sections.addSection(newLastSection);

        assertThat(sections.getSortedStations()).containsExactly(DEFAULT_FIRST_STATION, DEFAULT_LAST_STATION,
                newLastStation);
    }

    @DisplayName("신규 중간 역 구간 추가 시 역 목록 중간 항목 확인")
    @Test
    void addSectionWithNewMidStation() {
        Sections sections = makeSectionsForTest();

        Station newStation = new Station(5L, "서울역");
        Section newSection = new Section(2L, 1L, DEFAULT_FIRST_STATION, newStation, 5);
        sections.addSection(newSection);

        assertThat(sections.getSortedStations()).containsExactly(DEFAULT_FIRST_STATION, newStation,
                DEFAULT_LAST_STATION);
    }

    @DisplayName("중간 구간의 길이가 상위 구간 길이보다 긴 경우 Exception 발생 확인")
    @Test
    void addSectionWithOverDistance() {
        Sections sections = makeSectionsForTest();

        Station newStation = new Station(5L, "서울역");
        Section newSection = new Section(2L, 1L, DEFAULT_FIRST_STATION, newStation, 15);
        assertThatThrownBy(() -> sections.addSection(newSection)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("기존 구간과 연결점이 없는 구간 추가시 Exception 발생 확인")
    @Test
    void addSectionWithNotConnectedSection() {
        Sections sections = makeSectionsForTest();

        Station sadangStation = new Station(100L, "사당역");
        Station jamsilStation = new Station(200L, "잠실역");
        Section notConnectedSection = new Section(414L, 1L, sadangStation, jamsilStation, 300);
        assertThatThrownBy(() -> sections.addSection(notConnectedSection)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    private Sections makeSectionsForTest() {
        Sections sections = new Sections();
        Section section = new Section(1L, DEFAULT_LINE.getId(), DEFAULT_FIRST_STATION, DEFAULT_LAST_STATION, 10);
        sections.addSection(section);

        return sections;
    }
}
