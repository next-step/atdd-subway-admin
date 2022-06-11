package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineStationTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station station1 = new Station("신논현역");
    private final Station station2 = new Station("강남역");
    private final Station station3 = new Station("양재역");
    private final Long from1To2 = 10L;
    private final Long from2To3 = 15L;

    @Test
    void 노선_지하철역_이전_역과_그_거리_다음_역과_그_거리로_LineStation_객체가_생성되어야_한다() {
        // when
        final LineStation lineStation = givenLineStation();

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
        assertThat(lineStation.getLine()).isEqualTo(line);
        assertThat(lineStation.getStation()).isEqualTo(station2);
        assertThat(lineStation.getPrevious()).isEqualTo(station1);
        assertThat(lineStation.getDistanceToPrevious()).isEqualTo(from1To2);
        assertThat(lineStation.getNext()).isEqualTo(station3);
        assertThat(lineStation.getDistanceToNext()).isEqualTo(from2To3);
    }

    @Test
    void 이전_역과_그_거리를_변경할_수_있어야_한다() {
        // given
        final LineStation lineStation = givenLineStation();
        final Station newPrevious = new Station("new");
        final Long distance = 5L;

        // when
        lineStation.updatePrevious(newPrevious, distance);

        // then
        assertThat(lineStation.getPrevious()).isEqualTo(newPrevious);
        assertThat(lineStation.getDistanceToPrevious()).isEqualTo(distance);
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
    void 노선_지하철역_다음역_다음역과의_거리로_LineStation_객체가_생성되어야_한다() {
        // when
        final LineStation lineStation = newGivenLineStation();

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
    }

    @Test
    void 다음역이_있는지를_확인할_수_있어야_한다() {
        // given
        final LineStation lineStation1 = newGivenLineStation();
        final LineStation lineStation2 = new LineStation(line, station2, null, 0L);

        // when
        assertThat(lineStation1.hasNext()).isTrue();
        assertThat(lineStation2.hasNext()).isFalse();
    }

    private LineStation givenLineStation() {
        return new LineStation(line, station2, station1, from1To2, station3, from2To3);
    }

    private LineStation newGivenLineStation() {
        return new LineStation(line, station1, station2, from1To2);
    }
}
