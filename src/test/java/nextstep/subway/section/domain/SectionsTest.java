package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    Station 강남역;
    Station 삼성역;
    Station 종합운동장역;
    Station 잠실역;

    @BeforeEach
    public void init() {
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("종합운동장역");
        잠실역 = new Station("잠실역");
    }

    @DisplayName("Section들의 리스트를 보관하는 일급컬렉션")
    @Test
    void create() {
        int 구간거리 = 200;

        Section section = Section.create(강남역, 잠실역, 구간거리);
        Sections sections = new Sections();
        sections.addSection(section);

        SortedSet<Section> getSections = sections.getSections();

        assertThat(getSections).containsExactly(section);
    }

    @DisplayName("추가하려는 모든역이 존재할때 구간 추가실패")
    @Test
    void existAllStation() {

        int 구간거리 = 200;
        Section section = Section.create(강남역, 잠실역, 구간거리);
        Section newSection = Section.create(강남역, 잠실역, 구간거리);
        Sections sections = new Sections();
        sections.addSection(section);

        assertThatThrownBy(
                () -> sections.addSection(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 모든역이 존재하지않을때 구간 추가실패")
    @Test
    void notExistAllStation() {

        int 구간거리 = 200;
        Section section = Section.create(강남역, 잠실역, 구간거리);
        Section newSection = Section.create(삼성역, 종합운동장역, 구간거리);
        Sections sections = new Sections();
        sections.addSection(section);

        assertThatThrownBy(
                () -> sections.addSection(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}