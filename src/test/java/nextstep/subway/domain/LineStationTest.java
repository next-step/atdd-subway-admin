package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineStationTest {
    @Test
    void 노선과_지하철역_이전_지하철역으로_LineStation_객체가_생성되어야_한다() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Station station = new Station("강남역");
        final Station previous = new Station("신논현역");

        // when
        final LineStation lineStation = new LineStation(line, station, previous);

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
        assertThat(lineStation.getLine()).isEqualTo(line);
        assertThat(lineStation.getStation()).isEqualTo(station);
        assertThat(lineStation.getPrevious()).isEqualTo(previous);
    }
}
