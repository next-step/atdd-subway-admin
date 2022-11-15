package nextstep.subway.common.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class SubwayValidatorTest {

    @DisplayName("입력값이 null 또는 empty 이면 IllegalArgumentException 예외를 발생시킨다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @NullAndEmptySource
    void validateNotNullAndNotEmpty(String input) {
        Assertions.assertThatThrownBy(() -> SubwayValidator.validateNotNullAndNotEmpty(input))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
