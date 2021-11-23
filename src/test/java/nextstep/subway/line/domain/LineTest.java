package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.domain.StationTest.강남역;
import static nextstep.subway.station.domain.StationTest.역삼역;
import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    public static final Line LINE_2호선 = new Line("2호선", "green");

    @Test
    @DisplayName("구간을 포함한 노선 생성")
    void createLineAndSections() {
        Line line = new Line("2호선", "green");
        Section section = new Section(강남역, 역삼역, line, 10);
        line.addSection(section);

        Sections sections = new Sections();
        sections.add(section);

        assertThat(line.getSections()).isEqualTo(sections);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }
}
