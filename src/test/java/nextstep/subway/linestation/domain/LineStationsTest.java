package nextstep.subway.linestation.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.linestation.exception.DuplicateLineStationException;
import nextstep.subway.station.domain.Station;

public class LineStationsTest {

    @DisplayName("중복 LineStation 을 추가하면 예외가 발생하는지 테스트")
    @Test
    void given_DuplicateLineStation_when_AddLineStation_then_ThrownException() {
        // given
        final LineStation lineStation = new LineStation(new Line("1호선", "bg-blue-600"), new Station("신도림역"));
        final LineStation duplicateLineStation = new LineStation(new Line("1호선", "bg-blue-600"), new Station("신도림역"));
        final LineStations lineStations = new LineStations();
        lineStations.addLineStation(lineStation);

        // when
        final Throwable throwable = catchThrowable(() -> lineStations.addLineStation(duplicateLineStation));

        // then
        assertThat(throwable).isInstanceOf(DuplicateLineStationException.class);
    }

    @DisplayName("LineStations 에 특정 Station 이 포함되어 있는지 테스트")
    @Test
    void given_LineStations_when_ContainsStation_then_ReturnBoolean() {
        // given
        final Line line = new Line("1호선", "color");
        final Station 신도림역 = new Station("신도림역");
        final Station 서울역 = new Station("서울역");
        final LineStation lineStation = new LineStation(line, 신도림역);
        final LineStations lineStations = new LineStations();
        lineStations.addLineStation(lineStation);

        // when
        final boolean exist = lineStations.containsStation(new LineStation(line, 신도림역));
        final boolean notExist = lineStations.containsStation(new LineStation(line, 서울역));

        // then
        assertThat(exist).isEqualTo(true);
        assertThat(notExist).isEqualTo(false);
    }

    @DisplayName("LineStations 에 Station 이 몇 개 포함되었는지 확인하는 테스트")
    @Test
    void given_LineStations_when_CountStations_then_ReturnCountOfStations() {
        // given
        final Line line = new Line("1호선", "color");
        final Station 신도림역 = new Station("신도림역");
        final Station 서울역 = new Station("서울역");
        final LineStation lineStation = new LineStation(line, 신도림역);
        final LineStations lineStations = new LineStations();
        lineStations.addLineStation(lineStation);

        // when
        final long actual = lineStations.countStations(
            Arrays.asList(new LineStation(line, 신도림역), new LineStation(line, 서울역)));

        // then
        assertThat(actual).isEqualTo(1);
    }
}
