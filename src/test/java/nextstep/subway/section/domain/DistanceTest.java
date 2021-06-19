package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceTest {
    @DisplayName("정상 생성 테스트")
    @Test
    void create() {
        //given
        //when
        Distance distance = new Distance(7);
        //then
        assertThat(distance).isNotNull();
    }
}