package nextstep.subway;

import nextstep.subway.domain.LineName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineNameTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("지하철노선 이름 값 비어있을 경우 Exception 발생 확인")
    void validate_empty(String name) {
        assertThatThrownBy(() -> {
            new LineName(name);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1호선", "2호선"})
    @DisplayName("지하철노선 이름 생성후 값 비교 확인")
    void validateStringNumber(String expected) {
        LineName lineName = new LineName(expected);
        assertThat(lineName.getName()).isEqualTo(expected);
    }
}
