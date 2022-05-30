package nextstep.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void 구간의_길이가_양수가_아닐경우_예외가_발생한다() {
        // given
        Distance distance = new Distance(10);
        Distance newSectionDistance = new Distance(10);
        // when & then
        assertThatThrownBy(() ->
                distance.minus(newSectionDistance)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}