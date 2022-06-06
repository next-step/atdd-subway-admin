package nextstep.subway.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionRequestTest {
    @Test
    void Distance_가_NULL_일_수_없다() {
        assertThatThrownBy(() -> new SectionRequest(null, null, 1L, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void stationId_가_NULL_일_수_없다() {
        assertThatThrownBy(() -> new SectionRequest(null, null, null, 1)).isInstanceOf(IllegalArgumentException.class);
    }
}
