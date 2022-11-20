package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("거리에서 거리를 뺄 수 있다.")
    @Test
    void subtract_test() {
        Distance 기존_거리 = Distance.from(10);
        Distance 차간_거리 = Distance.from(5);
        기존_거리.subtract(차간_거리);

        assertThat(기존_거리).isEqualTo(Distance.from(5));
    }

    @DisplayName("거리에서 거리를 뺄때 빼고자 하는 거리가 같거나 크면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(ints ={10, 12})
    void subtract_exception(int 거리) {
        Distance 기존_거리 = Distance.from(10);
        Distance 차간_거리 = Distance.from(거리);

        assertThatThrownBy(() ->기존_거리.subtract(차간_거리))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("거리를 생성할때 거리가 양수가 아니면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(ints ={0, -1})
    void create_exception(int 거리) {

        assertThatThrownBy(() ->Distance.from(거리))
                .isInstanceOf(IllegalArgumentException.class);
    }

}