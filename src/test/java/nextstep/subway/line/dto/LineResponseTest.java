package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 response 테스트")
class LineResponseTest {

    @Test
    @DisplayName("Line entity를 이용하여 지하철 노선 response 생성")
    void of() {
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "양재역");
        Section section = new Section(upStation, downStation, 10);
        LineStation lineStation1 = new LineStation(upStation, null, 0);
        LineStation lineStation2 = new LineStation(downStation, upStation, 10);
        Line line = new Line("신분당선", "bg-red-600");
        line.addSection(section);
        line.addLineStation(lineStation1);
        line.addLineStation(lineStation2);
        LineResponse lineResponse = LineResponse.of(line);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }
}
