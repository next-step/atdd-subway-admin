package nextstep.subway.line.domain.wrappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 거리 원시값 포장 객체 테스트")
class DistanceTest {

    @Test
    @DisplayName("구간 거리 원시값 포장 객체 생성")
    void create() {
        Distance distance = new Distance(7);
        assertThat(distance).isEqualTo(new Distance(7));
    }

    @Test
    @DisplayName("구간 거리 생성 시 값으로 음수 입력 시 에러 발생")
    void invalid() {
        assertThatThrownBy(() -> new Distance(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간거리는 값은 음수가 입력할 수 없습니다.");
    }

    @ParameterizedTest
    @DisplayName("두개의 구간 거리 값 빼기")
    @CsvSource(value = {"7:3", "8:2"}, delimiter = ':')
    void subtractionDistance(int value, int expected) {
        Distance distance = new Distance(10);
        assertThat(distance.subtractionDistance(new Distance(value))).isEqualTo(new Distance(expected));
    }

    @ParameterizedTest
    @DisplayName("두개의 구간 거리 값 더하기")
    @CsvSource(value = {"7:17", "8:18"}, delimiter = ':')
    void sumDistance(int value, int expected) {
        Distance distance = new Distance(10);
        assertThat(distance.sumDistance(new Distance(value))).isEqualTo(new Distance(expected));
    }


    @ParameterizedTest
    @DisplayName("두개의 구간 거리 뺄셈 결과가 0이나 음수인 경우 에러 발생")
    @ValueSource(ints = {7, 8})
    void valid_distance(int value) {
        Distance distance = new Distance(7);
        assertThatThrownBy(() -> distance.subtractionDistance(new Distance(value)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 사이에 새로운 역을 등록 시 구간거리는 기존 등록된 구간 거리보다 작아야합니다.");
    }
}