package nextstep.subway.common;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NameTest {

    @DisplayName("이름이 빈 값이면 생성을 실패한다.")
    @Test
    void create_Empty_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Name(null)
        );
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new Name("")
        );
    }

}
