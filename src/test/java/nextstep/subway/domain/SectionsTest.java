package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {
    private Station 상행종점역;
    private Station 하행종점역;
    private Station 새로운역;
    private Section initSection;

    @BeforeEach
    void setUp() {
        상행종점역 = new Station("상행종점역");
        하행종점역 = new Station("하행종점역");
        새로운역 = new Station("새로운역");
        initSection = Section.of(상행종점역, 하행종점역, 10);
    }

    @Test
    @DisplayName("sections의 stations 조회")
    void findSectionsStations() {
        Sections sections = new Sections(initSection);
        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(상행종점역,하행종점역);
    }

    @Test
    @DisplayName("sections의 새로운 section 추가")
    void checkSection() {
        Sections sections = new Sections(initSection);
        Section newSection = Section.of(새로운역, 상행종점역, 8);
        sections.add(newSection);
        assertThat(sections.getStations()).containsExactly(새로운역,상행종점역,하행종점역);
    }

    @Test
    @DisplayName("새로운 station을 새로운 하행종점으로 등록")
    void addDownStationSection() {
        Sections sections = new Sections(initSection);
        Section newSection = Section.of(하행종점역, 새로운역, 8);
        sections.add(newSection);
        assertThat(sections.getStations()).containsExactly(상행종점역,하행종점역,새로운역);
    }

    @Test
    @DisplayName("새로운 station을 상행종점, 하행 종점 사이에 등록")
    void addMiddleStationSection() {
        Sections sections = new Sections(initSection);
        Section newSection = Section.of(상행종점역, 새로운역, 8);
        sections.add(newSection);
        assertThat(sections.getStations()).containsExactly(상행종점역,새로운역,하행종점역);
    }

    @Test
    @DisplayName("기존 section 길이보다 추가 section 길이가 크거나 같으면 error 발생")
    void error_addSectionLongThanOriginSection() {
        Sections sections = new Sections(initSection);
        Section newSection = Section.of(상행종점역, 새로운역, 10);
        assertThrows(IllegalArgumentException.class, () -> {
            sections.add(newSection);
        });
    }

}
