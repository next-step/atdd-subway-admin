package nextstep.subway.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionRequestTest {
    @Test
    void Distance_가_NULL_이거나_0_이하_일_수_없다() {
        assertThatThrownBy(() -> new SectionRequest(1L, 2L, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SectionRequest(1L, 2L, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SectionRequest(1L, 2L, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void stationId_가_NULL_이거나_0_이하_일_수_없다() {
        assertThatThrownBy(() -> new SectionRequest(2L, null, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SectionRequest(2L, 0L, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SectionRequest(2L, -1L, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void preStationId_가_0_이하_일_수_없다() {
        assertThatThrownBy(() -> new SectionRequest(-1L, 1L, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SectionRequest(0L, 1L, 1)).isInstanceOf(IllegalArgumentException.class);
    }

}
