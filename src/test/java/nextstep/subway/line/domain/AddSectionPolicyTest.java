package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddSectionPolicyTest {
    @DisplayName("상행역을 기준으로 새로운 구간을 추가할 수 있다.")
    @Test
    void addSectionWithUpStationTest() {
        Section originalSection = new Section(1L, 2L, 3L);
        Section addSection = new Section(1L, 3L, 2L);

        AddSectionPolicy.ADD_WITH_UP_STATION.calculateOriginalSection(originalSection, addSection);

        assertThat(originalSection).isEqualTo(new Section(3L, 2L, 1L));
    }
}