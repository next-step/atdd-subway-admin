package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineStationTest {
    private final Line line = new Line("신분당선", "red");
    private final Station station1 = new Station("신논현역");
    private final Station station2 = new Station("강남역");
    private final Long from1To2 = 10L;

    @Test
    void 노선_지하철역_다음역_다음역과의_거리로_LineStation_객체가_생성되어야_한다() {
        // when
        final LineStation lineStation = givenLineStation();

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
    }

    @Test
    void 다음_역과_그_거리를_변경할_수_있어야_한다() {
        // given
        final LineStation lineStation = givenLineStation();
        final Station newNext = new Station("new");
        final Long distance = 5L;

        // when
        lineStation.updateNext(newNext, distance);

        // then
        assertThat(lineStation.getNext()).isEqualTo(newNext);
        assertThat(lineStation.getDistanceToNext()).isEqualTo(distance);
    }

    @Test
    void 다음역이_있는지를_확인할_수_있어야_한다() {
        // given
        final LineStation lineStation1 = givenLineStation();
        final LineStation lineStation2 = new LineStation(line, station2, null, 0L);

        // when
        assertThat(lineStation1.hasNext()).isTrue();
        assertThat(lineStation2.hasNext()).isFalse();
    }

    private LineStation givenLineStation() {
        return new LineStation(line, station1, station2, from1To2);
    }
}
