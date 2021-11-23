package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineTest {
    @DisplayName("노선명, 노선색상을 변경한다")
    @Test
    void testUpdate() {
        // given
        Line line = new Line("박달-강남선", "blue");
        Line toBeLine = new Line("박달-강남선", "blue");

        // when
        Line updatedLine = line.update(toBeLine);

        // then
        assertAll(
                () -> assertThat(updatedLine).isEqualTo(line),
                () -> assertThat(updatedLine.getName()).isEqualTo(toBeLine.getName()),
                () -> assertThat(updatedLine.getColor()).isEqualTo(toBeLine.getColor())
        );
    }

    @DisplayName("Section 객체로 구간을 추가한다")
    @Test
    void testAddSectionObject() {
        // given
        Line line = new Line("박달-강남선", "blue");
        Section section = new Section(line, new Station("1"), new Station("2"), 100);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).hasSize(2);
    }

    @DisplayName("상행선, 하행선, 거리 값으로 구간을 추가한다")
    @Test
    void testAddSectionParameters() {
        // given
        Line line = new Line("박달-강남선", "blue");

        // when
        line.addSection(new Station("1"), new Station("2"), 100);

        // then
        assertThat(line.getStations()).hasSize(2);
    }
}
