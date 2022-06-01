package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineStationTest {
    @Test
    void 노선과_지하철역_객체를_파라미터로_LineStation_객체가_생성되어야_한다() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Station station = new Station("강남역");

        // when
        final LineStation lineStation = new LineStation(line, station);

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
    }

}
