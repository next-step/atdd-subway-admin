package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    @DisplayName("노선 이름과 노선 색을 변경한다.")
    void update() {
        // given
        Line line = new Line("4호선", "bg-skyblue-600");
        String name = "1호선";
        String color = "bg-blue-600";

        // when
        line.update(new Line(name, color));

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void addSection() {
        // given
        Line line = new Line();
        Station upStation = new Station();
        Station downStation = new Station();
        Section section = new Section(upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().get(0)).isEqualTo(section);
    }
}