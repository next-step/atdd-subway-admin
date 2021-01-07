package nextstep.subway.section.domain;

import nextstep.subway.section.application.CannotRemoveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SectionsTest {
    @Test
    @DisplayName("구간 삭제 - 하나 남은 구간의 역 삭제시 익셉션")
    void removeLastSectionThenException() {
        Sections sections = new Sections();
        Section section = new Section(1L, 2L, 10);
        sections.addSection(section);

        assertThrows(CannotRemoveException.class, () -> sections.removeSectionByStationId(2L));
    }

    @Test
    @DisplayName("구간 삭제 - 시작역 일때 해당 구간만 제거")
    void removeFirstSectionStation() {
        Section firsSection = new Section(1L, 2L, 10);
        Section secondSection = new Section(2L, 4L, 10);
        Section thirdSection = new Section(4L, 6L, 10);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(firsSection, secondSection, thirdSection)));

        sections.removeSectionByStationId(1L);

        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(secondSection);
        assertThat(results.get(1)).isEqualTo(thirdSection);
    }

    @Test
    @DisplayName("구간 삭제 - 종착역일 때 해당 구간만 제거")
    void removeLastSection() {
        Section firsSection = new Section(1L, 2L, 10);
        Section secondSection = new Section(2L, 4L, 10);
        Section thirdSection = new Section(4L, 6L, 10);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(firsSection, secondSection, thirdSection)));

        sections.removeSectionByStationId(6L);

        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(firsSection);
        assertThat(results.get(1)).isEqualTo(secondSection);
    }

    @Test
    @DisplayName("구간 삭제 - 중간역일때 해당 구간 제거 후 양옆 구간 연결")
    void removeMiddleSection() {
        Section firsSection = new Section(1L, 2L, 10);
        Section secondSection = new Section(2L, 4L, 10);
        Section thirdSection = new Section(4L, 6L, 10);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(firsSection, secondSection, thirdSection)));

        sections.removeSectionByStationId(2L);

        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(new Section(1L, 4L, 20)));
        assertThat(results.contains(new Section(4L, 6L, 10)));
    }

    @Test
    @DisplayName("사이 구간 등록 - 상행역이 같은 구간을 등록시")
    void addSection_whenSameUpStation() {
        Section firsSection = new Section(1L, 6L, 10);
        Section secondSection = new Section(1L, 4L, 5);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(firsSection)));

        sections.addSection(secondSection);

        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(new Section(1L, 4L, 5)));
        assertThat(results.contains(new Section(4L, 6L, 5)));
    }

    @Test
    @DisplayName("사이 구간 등록 - 하행역이 같은 구간을 등록시")
    void addSection_whenSameDownStation() {
        Section firsSection = new Section(1L, 6L, 10);
        Section secondSection = new Section(4L, 6L, 5);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(firsSection)));

        sections.addSection(secondSection);

        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(new Section(1L, 4L, 5)));
        assertThat(results.contains(new Section(4L, 6L, 5)));
    }
}