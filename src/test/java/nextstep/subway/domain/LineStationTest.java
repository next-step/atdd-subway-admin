package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineStationTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station sinnonhyun = new Station("신논현역");
    private final Station gangnam = new Station("강남역");
    private final Station yangjae = new Station("양재역");

    @Test
    void 노선과_지하철역과_이전_역과_다음_역으로_LineStation_객체가_생성되어야_한다() {
        // when
        final LineStation lineStation = new LineStation(line, gangnam, sinnonhyun, yangjae);

        // then
        assertThat(lineStation).isNotNull();
        assertThat(lineStation).isInstanceOf(LineStation.class);
        assertThat(lineStation.getLine()).isEqualTo(line);
        assertThat(lineStation.getStation()).isEqualTo(gangnam);
        assertThat(lineStation.getPrevious()).isEqualTo(sinnonhyun);
        assertThat(lineStation.getNext()).isEqualTo(yangjae);
    }

    @Test
    void 이전_역과_다음_역을_변경할_수_있어야_한다() {
        // given
        final LineStation lineStation = new LineStation(line, gangnam, null, null);

        // when
        lineStation.update(sinnonhyun, yangjae);

        // then
        assertThat(lineStation.getPrevious()).isEqualTo(sinnonhyun);
        assertThat(lineStation.getNext()).isEqualTo(yangjae);
    }
}
