package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("기존 구간의 상행역을 포함하여 새로운 구간이 들어오게 되면, 기존 구간의 상행역과 하행역이 변경되어야 한다")
    void should_rebase_section_added_with_upStation() {
        // given
        Section lineSection = getSection(1L, 3L, 100L);
        Section addedSection = getSection(1L, 2L, 40L);

        // when
        lineSection.rebase(addedSection);

        // then
        assertThat(lineSection.getDistance().getDistance()).isEqualTo(60L);
        assertThat(lineSection.getUpStation()).isEqualTo(new Station(2L));
    }

    @Test
    @DisplayName("기존 구간의 하행역을 포함하여 새로운 구간이 들어오게 되면, 기존 구간의 상행역과 하행역이 변경되어야 한다")
    void should_rebase_section_added_with_downStation() {
        // given
        Section lineSection = getSection(1L, 3L, 100L);
        Section addedSection = getSection(2L, 3L, 40L);

        // when
        lineSection.rebase(addedSection);

        // then
        assertThat(lineSection.getDistance().getDistance()).isEqualTo(60L);
        assertThat(lineSection.getDownStation()).isEqualTo(new Station(2L));
    }

    private Section getSection(Long upStationId, Long downStationId, Long distance) {
        return new Section(new Line(), distance, new Station(upStationId), new Station(downStationId));
    }

}
