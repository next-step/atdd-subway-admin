package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @Test
    @DisplayName("노선 이름과 노선 색을 변경한다.")
    void update() {
        // given
        Line line = Line.of("4호선", "bg-skyblue-600");
        String name = "1호선";
        String color = "bg-blue-600";

        // when
        line.update(Line.of(name, color));

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("노선에 구간을 추가한다.")
    void addSection() {
        // given
        Line line = new Line();
        Section newSection = createSection(line);

        // when
        line.addSection(newSection);

        // then
        assertThat(line.getSections().getSections().get(0)).isEqualTo(newSection);
    }

    @Test
    @DisplayName("추가할 구간의 노선과 현재 노선이 다를 경우 실패한다.")
    void addSection_다른_노선() {
        // given
        Line line = new Line();
        Section newSection = createSection(new Line());

        // when, then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가할 노선과 현재 노선이 다릅니다.");
    }

    private Section createSection(Line line) {
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        return new Section(upStation, downStation, 10, line);
    }
}