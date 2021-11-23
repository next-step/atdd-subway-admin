package nextstep.subway.line.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.ServiceException;

class DistanceTest {

    @Test
    void 거리는_0보다_작을_수_없다() {
        // then
        Assertions.assertThatThrownBy(() -> {
                      Distance distance = new Distance(-1);
                  }).isInstanceOf(ServiceException.class)
                  .hasMessage("구간의 거리가 0 또는 0보다 작을 수 없습니다.");
    }
}