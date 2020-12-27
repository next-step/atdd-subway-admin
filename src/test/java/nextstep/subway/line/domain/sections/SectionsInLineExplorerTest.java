package nextstep.subway.line.domain.sections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsInLineExplorerTest {
    private static final SectionsInLineExplorer sectionExplorer = new SectionsInLineExplorer();

    @DisplayName("구간 순서대로 정렬된 역 목록을 구할 수 있다.")
    @Test
    void getStationIdsOrderBySectionTest() {
        Section section1 = new Section(1L, 4L, 10L);
        Section section2 = new Section(4L, 3L, 10L);
        Section section3 = new Section (3L, 2L, 10L);
        Sections sections = new Sections(new ArrayList<>(Arrays.asList(section1, section2, section3)));

        List<Long> stationIds = sectionExplorer.getStationIdsOrderBySection(sections);

        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(stationIds.size() - 1)).isEqualTo(2L);
    }
}