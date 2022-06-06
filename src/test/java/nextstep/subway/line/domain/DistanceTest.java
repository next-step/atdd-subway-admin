package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void 유효범위_오류_테스트() {
        assertThatThrownBy(() -> new Distance(-10L))
                .isInstanceOf(LineException.class);
    }
}