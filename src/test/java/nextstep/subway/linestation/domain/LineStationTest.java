package nextstep.subway.linestation.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineStationTest {

    @DisplayName("previous 또는 next 호출 시 null 을 전달할 경우 예외가 발생하는지 테스트")
    @Test
    void given_NullStationOrDistance_when_PreviousOrNext_then_ThrownException() {
        // given
        final Line line = new Line("1호선", "bg-blue-600");
        final Station station = new Station("강남역");
        final Integer previousDistance = 100;
        final Station nextStation = new Station("신도림");
        final LineStation lineStation = new LineStation(line, station);

        // when
        final Throwable prevThrowable = catchThrowable(() -> lineStation.previous(null, previousDistance));
        final Throwable nextThrowable = catchThrowable(
            () -> lineStation.next(new LineStation(line, nextStation), null));

        // then
        assertAll(
            () -> assertThat(prevThrowable).isInstanceOf(IllegalArgumentException.class),
            () -> assertThat(nextThrowable).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("주어진 LineStation 이 같은 Line 에 속했는지 확인하는 테스트")
    @Test
    void given_LineStation_when_VerifySameLineOrNot_then_ReturnBoolean() {
        // given
        final Line line = new Line("1호선", "bg-blue-600");
        final LineStation lineStation = new LineStation(line, new Station("신도림역"));
        final LineStation sameLineStation = new LineStation(line, new Station("서울역"));
        final Line anotherLine = new Line("2호선", "bg-color-600");

        // when
        final boolean different = lineStation.isSameLine(anotherLine);
        final boolean same = lineStation.isSameLine(line);

        // then
        assertThat(different).isEqualTo(false);
        assertThat(same).isEqualTo(true);
    }

    @DisplayName("이전 역이나 다음 역과 일치하는 역으로 업데이트하면 이전 혹은 다음 역이 변경되는지 테스트")
    @Test
    void given_StationEqualPrevOrNext_when_UpdateLineStation_then_PrevOrNextStationIsChanged() {
        // given
        final Station 구로역 = new Station("구로역");
        final Station 구일역 = new Station("구일역");
        final Station 영등포역 = new Station("영등포역");
        final Station 신길역 = new Station("신길역");
        final Line line = new Line("1호선", "bg-blue-600");
        final LineStation lineStation = new LineStation(line, new Station("신도림역"));
        lineStation.previous(new LineStation(line, 구로역), 100);
        lineStation.next(new LineStation(line, 신길역), 200);
        final int previousDistance = 30;
        final int nextDistance = 20;

        // when
        lineStation.update(new LineStation(line, 구로역), new LineStation(line, 구일역), previousDistance);
        lineStation.update(new LineStation(line, 영등포역), new LineStation(line, 신길역), nextDistance);

        // then
        assertAll(
            () -> assertThat(lineStation.getPreviousStation().orElse(null)).isEqualTo(new LineStation(line, 구일역)),
            () -> assertThat(lineStation.getPreviousDistance().orElse(null)).isEqualTo(100 - previousDistance),
            () -> assertThat(lineStation.getNextStation().orElse(null)).isEqualTo(new LineStation(line, 영등포역)),
            () -> assertThat(lineStation.getNextDistance().orElse(null)).isEqualTo(200 - nextDistance)
        );
    }

    @DisplayName("이전 혹은 다음 역 업데이트 조건에 해당하지 않는 역으로 업데이트할 경우 테스트")
    @Test
    void given_StationsNotUpdatable_when_UpdateLineStation_then_PrevOrNextStationIsNotChanged() {
        // given
        final Station 구로역 = new Station("구로역");
        final Station 구일역 = new Station("구일역");
        final Station 영등포역 = new Station("영등포역");
        final Station 신길역 = new Station("신길역");
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Line line = new Line("1호선", "bg-blue-600");
        final LineStation lineStation = new LineStation(line, new Station("신도림역"));
        lineStation.previous(new LineStation(line, 구로역), 100);
        lineStation.next(new LineStation(line, 신길역), 200);
        final int nextDistance = 20;
        final int previousDistance = 50;

        // when
        lineStation.update(new LineStation(line, 강남역), new LineStation(line, 역삼역), previousDistance);
        lineStation.update(new LineStation(line, 구일역), new LineStation(line, 구로역), previousDistance);
        lineStation.update(new LineStation(line, 신길역), new LineStation(line, 영등포역), previousDistance);

        // then
        assertAll(
            () -> assertThat(lineStation.getPreviousStation().orElse(null)).isEqualTo(new LineStation(line, 구로역)),
            () -> assertThat(lineStation.getPreviousDistance().orElse(null)).isEqualTo(100),
            () -> assertThat(lineStation.getNextStation().orElse(null)).isEqualTo(new LineStation(line, 신길역)),
            () -> assertThat(lineStation.getNextDistance().orElse(null)).isEqualTo(200)
        );
    }

    @DisplayName("LineStation 의 역과 일치하는 역으로 업데이트했을 때 변경되는지 테스트")
    @Test
    void given_SameStation_when_UpdateLineStation_then_PrevOrNextStationChanged() {
        // given
        final Station 구로역 = new Station("구로역");
        final Station 구일역 = new Station("구일역");
        final Station 영등포역 = new Station("영등포역");
        final Station 신길역 = new Station("신길역");
        final Station 신도림역 = new Station("신도림역");
        final Line line = new Line("1호선", "bg-blue-600");
        final LineStation lineStation = new LineStation(line, 신도림역);
        lineStation.previous(new LineStation(line, 구로역), 100);
        lineStation.next(new LineStation(line, 신길역), 200);
        final int nextDistance = 20;
        final int previousDistance = 30;

        // when
        lineStation.update(new LineStation(line, 구일역), new LineStation(line, 신도림역), previousDistance);
        lineStation.update(new LineStation(line, 신도림역), new LineStation(line, 영등포역), nextDistance);

        // then
        assertAll(
            () -> assertThat(lineStation.getPreviousStation().orElse(null)).isEqualTo(new LineStation(line, 구일역)),
            () -> assertThat(lineStation.getPreviousDistance().orElse(null)).isEqualTo(previousDistance),
            () -> assertThat(lineStation.getNextStation().orElse(null)).isEqualTo(new LineStation(line, 영등포역)),
            () -> assertThat(lineStation.getNextDistance().orElse(null)).isEqualTo(nextDistance)
        );
    }
}
