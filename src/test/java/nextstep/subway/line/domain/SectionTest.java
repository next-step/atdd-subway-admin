package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    @DisplayName("Section끼리 상행역이 일치하는지 확인할 수 있다.")
    @Test
    void isUpStationSameTest() {
        Section section1 = new Section(1L, 2L, 10L);
        Section section2 = new Section(1L, 3L, 10L);

        assertThat(section1.isSameUpStation(section2)).isTrue();
    }

    @DisplayName("Section끼리 하행역이 일치하는지 확인할 수 있다.")
    @Test
    void isDownStationSameTest() {
        Section section1 = new Section(1L, 2L, 10L);
        Section section2 = new Section(3L, 2L, 10L);

        assertThat(section1.isSameDownStation(section2)).isTrue();
    }

    @DisplayName("Section 간 비교를 통해 Section 추가 정책을 판단할 수 있다.")
    @Test
    void getSectionAddStrategyTest() {

    }
}