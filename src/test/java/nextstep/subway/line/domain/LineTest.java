package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선")
class LineTest {
    @Test
    @DisplayName("노선 이름과 색상을 수정할 수 있다.")
    void 노선_수정() {
        //String name, String color, Station upStation, Station downStation, int distance
        Station upStation = new Station();
        Station downStation = new Station();

        Line line = Line.of("분당선", "bg-red-600", upStation, downStation, 10);
        line.update("다른분당선", "bg-red-200");

        assertAll(() -> assertThat(line.getName()).isEqualTo("다른분당선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-red-200"));
    }
}
