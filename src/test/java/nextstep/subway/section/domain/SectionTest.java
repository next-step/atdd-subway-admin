package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 테스트")
public class SectionTest {

    @DisplayName("상행선과 하행선이 같을경우 예외 발생")
    @Test
    void createWrong() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1000))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
