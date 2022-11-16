package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @DisplayName("노선의 이름과 색깔을 수정할 수 있다")
    @Test
    void update() {
        // given
        Line line = new Line("신분당선", "빨강색");

        // when
        line.update("3호선", "노랑색");

        assertAll(
                () -> assertThat(line.getName()).isEqualTo("3호선"),
                () -> assertThat(line.getColor()).isEqualTo("노랑색")
        );
    }

    @DisplayName("노선에 포함된 전체 지하철역을 알 수 있다")
    @Test
    void findStations() {
        // given
        Line line = new Line("신분당선", "빨강색");
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).contains(upStation, downStation);
    }
}
