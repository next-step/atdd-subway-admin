package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.linestation.domain.LineStation;
import nextstep.subway.station.domain.Station;

public class LineTest {

    @DisplayName("다른 노션 LineStation 을 추가하려고 하면 예외가 발생하는지 테스트")
    @Test
    void given_DifferentLineStation_when_AddLineStation_thenThrownException() {
        // given
        final Line line = new Line("1호선", "bg-blue-600");
        final Line anotherLine = new Line("2호선", "bg-green-600");
        final LineStation differentLineStation = new LineStation(anotherLine, new Station("대림역"));

        // when
        final Throwable throwable = catchThrowable(() -> line.addLineStation(differentLineStation));

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }
}
