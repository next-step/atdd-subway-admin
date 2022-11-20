package nextstep.subway.domain;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("기존 구간의 상행역을 포함하여 새로운 구간이 들어오게 되면, 기존 구간의 상행역과 하행역이 변경되어야 한다")
    void should_rebase_section_added_with_upStation() {
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));
        sections.addSection(createSection(3L, 5L, 50L));

        // when
        sections.addSection(createSection(2L, 3L, 60L));

        // then
        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(1L, 2L, 3L, 5L));
    }

    @Test
    @DisplayName("기존 구간의 하행역을 포함하여 새로운 구간이 들어오게 되면, 기존 구간의 상행역과 하행역이 변경되어야 한다")
    void should_rebase_section_added_with_downStation() {
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));
        sections.addSection(createSection(3L, 5L, 50L));

        // when
        sections.addSection(createSection(4L, 5L, 60L));

        // then
        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(1L, 3L, 4L, 5L));
    }

    private Section createSection(Long upStationId, Long downStationId, Long distance) {
        return new Section(new Line(), distance, new Station(upStationId), new Station(downStationId));
    }

    private List<Station> createStations(Long... ids) {
        return Arrays.stream(ids)
                .map(Station::new).
                collect(Collectors.toList());
    }
}
