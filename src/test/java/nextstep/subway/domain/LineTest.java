package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class LineTest {
    @Test
    void 노선_이름과_색상을_파라미터로_Line_객체가_생성되어야_한다() {
        // given
        final String lineName = "신분당선";
        final String color = "bg-red-600";

        // when
        final Line line = new Line(lineName, color);

        // then
        assertThat(line).isNotNull();
        assertThat(line).isInstanceOf(Line.class);
    }

    @Test
    void 노선에_지하철역과의_연관관계를_추가할_수_있어야_한다() {
        // given
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("양재역");
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        line.relateToStation(new LineStation(line, station1));
        line.relateToStation(new LineStation(line, station2));

        // then
        assertThat(line.getLineStations().getStations()).containsExactly(station1, station2);
    }

    @Test
    void 다른_노선과_자하철역의_연관관계를_추가하면_익셉션이_발생해야_한다() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final LineStation lineStation = new LineStation(new Line("2호천", "bg-green-600"),
                new Station("강남역"));

        // when and then
        assertThatThrownBy(() -> line.relateToStation(lineStation))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
