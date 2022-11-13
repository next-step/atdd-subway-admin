package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @Test
    @DisplayName("구간 거리 변경 성공")
    void change() {
        Distance distance = new Distance(5);

        distance.change(10);

        assertThat(distance.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("기존 구간의 거리보다 크거나 같으면 exception 처리")
    void equalToOrLessThan() {
        Distance distance = new Distance(5);

        assertThatThrownBy(() -> distance.equalToOrLessThan(6)).isInstanceOf(IllegalArgumentException.class);
    }
}
