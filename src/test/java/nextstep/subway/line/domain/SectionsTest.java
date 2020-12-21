package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("중복 없이 역 ID 목록을 가져올 수 있다.")
    @Test
    void getStationsTest() {
        Long station1Id = 1L;
        Long station2Id = 2L;
        Long station3Id = 3L;
        int expectedSize = 3;

        Sections sections = new Sections();
        sections.initFirstSection(new Section(1L, station1Id, station2Id, 3L));
        sections.initFirstSection(new Section(2L, station2Id, station3Id, 5L));

        List<Long> stationIds = sections.getStationIds();

        assertThat(stationIds).hasSize(expectedSize);
    }

    @DisplayName("Sections 일급 컬렉션 내 Section이 하나도 없는 상태에서 추가 작업을 진행할 수 없다.")
    @Test
    void addFailWhenSectionsEmptyTest() {
        Sections sections = new Sections();

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 2L, 3L)))
                .isInstanceOf(InvalidSectionsActionException.class);
    }

    // TODO: 기능을 좀 더 세분화 해야한다.
    @DisplayName("기존역 중 상행역과 일치하는 Section을 추가할 수 있다.")
    @Test
    void addWhenSectionSameWithUpStationTest() {
        Sections sections = new Sections();
        sections.initFirstSection(new Section(1L, 2L, 10L));

        sections.addSection(new Section(1L, 3L, 5L));

        assertThat(sections.contains(new Section(1L, 3L, 5L))).isTrue();
        assertThat(sections.contains(new Section(3L, 2L, 5L))).isTrue();
    }

    @DisplayName("기존역 중 하행역과 일치하는 Section을 추가할 수 있다.")
    @Test
    void addWhenSectionSameWithDownStationTest() {

    }

    @DisplayName("전달된 Section과 상행역, 하행역 중 하나라도 일치하는 Section을 모두 찾아낸다.")
    @Test
    void findCandidateSectionsTest() {
        Section targetSection = new Section(1L, 2L, 3L);
        Section section12 = new Section(1L, 2L, 3L);
        Section section21 = new Section(2L, 1L, 3L);
        Section section13 = new Section(1L, 3L, 3L);
        Section section32 = new Section(3L, 2L, 3L);
        Section section34 = new Section(3L, 4L, 3L);

        Sections sections = new Sections(Arrays.asList(section12, section21, section13, section32, section34));

        List<Section> candidateSections = sections.findCandidateSections(targetSection);

        assertThat(candidateSections).contains(section12, section13, section32);
    }
}