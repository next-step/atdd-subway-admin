package nextstep.subway.line.domain;

import nextstep.subway.common.message.ExceptionMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LineNameTest {

    @DisplayName("지하철 노선 이름 값이 null 이거나 empty 이면 LineName 객체 생성 시 예외가 발생한다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @NullAndEmptySource
    void nullOrEmptyName(String input) {
        Assertions.assertThatThrownBy(() -> LineName.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(ExceptionMessage.REQUIRED);
    }

    @DisplayName("지하철 노선 이름 값이 null 과 empty 가 아니면 LineName 객체가 정상적으로 생성된다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(strings = {"신분당선", "분당선", "1호선"})
    void createLineName(String input) {
        LineName name = LineName.from(input);

        Assertions.assertThat(name).isNotNull();
    }

    @DisplayName("지하철 노선 이름 값이 다른 LineName 객체는 동등하지 않다.")
    @Test
    void equalsFail() {
        LineName name1 = LineName.from("신분당선");
        LineName name2 = LineName.from("분당선");

        Assertions.assertThat(name1).isNotEqualTo(name2);
    }

    @DisplayName("지하철 노선 이름 값이 같은 LineName 객체는 동등하다.")
    @Test
    void equalsSuccess() {
        LineName name1 = LineName.from("신분당선");
        LineName name2 = LineName.from("신분당선");

        Assertions.assertThat(name1).isEqualTo(name2);
    }
}
