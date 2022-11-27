package nextstep.subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        sections.addSection(createSection(4L, 5L, 30L));

        // then
        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(1L, 3L, 4L, 5L));
    }

    @Test
    @DisplayName("기존 구간에서 특정 구간을 지우게 되면, 특정 구간을 제외한 역들이 정상적으로 연결되어야 한다")
    void should_replace_with_other_section_when_remove_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));
        sections.addSection(createSection(3L, 5L, 50L));
        sections.addSection(createSection(5L, 10L, 10L));

        // when
        sections.removeSectionThatContains(createStation(3L));

        // then

        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(1L, 5L, 10L));
    }

    @Test
    @DisplayName("기존 구간에서 상행 종점 구간을 지우게 되면, 종점 구간을 제외한 역들이 정상적으로 연결되어야 한다")
    void should_replace_with_other_section_when_remove_last_up_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));
        sections.addSection(createSection(3L, 5L, 50L));

        // when
        sections.removeSectionThatContains(createStation(1L));

        // then

        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(3L, 5L));
    }

    @Test
    @DisplayName("기존 구간에서 하행 종점 구간을 지우게 되면, 종점 구간을 제외한 역들이 정상적으로 연결되어야 한다")
    void should_replace_with_other_section_when_remove_last_down_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));
        sections.addSection(createSection(3L, 5L, 50L));

        // when
        sections.removeSectionThatContains(createStation(5L));

        // then

        assertThat(sections.getOrderedStations()).containsExactlyElementsOf(createStations(1L, 3L));
    }

    @Test
    @DisplayName("구간이 하나일 경우, 종점역을 지우게 되면, 에러가 발생해야 한다")
    void throws_exception_when_remove_last_station_of_last_section() {
        // given
        Sections sections = new Sections();
        sections.addSection(createSection(1L, 3L, 100L));

        // when && then
        assertThatThrownBy(() -> sections.removeSectionThatContains(createStation(1L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Section createSection(Long upStationId, Long downStationId, Long distance) {
        return new Section(new Line(), distance, new Station(upStationId), new Station(downStationId));
    }

    private List<Station> createStations(Long... ids) {
        return Arrays.stream(ids)
                .map(this::createStation).
                collect(Collectors.toList());
    }

    private Station createStation(Long id) {
        return new Station(id);
    }
}
