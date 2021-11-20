package nextstep.subway.station.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.domain.StationTest.강남역;
import static nextstep.subway.station.domain.StationTest.역삼역;
import static org.assertj.core.api.Assertions.assertThat;

public class StationResponsesTest {
    @Test
    @DisplayName("노선 구간을 지하철 역 리스트로 나열")
    void ofByLineSectionsTest() {
        Line line = new Line("2호선", "green");
        Section section = new Section(강남역, 역삼역, line, 10);
        line.addSection(section);

        StationResponses stationResponses = StationResponses.ofByLineSections(line.getSections());

        assertThat(stationResponses.getStationResponses())
                .contains(
                        StationResponse.of(강남역)
                        , StationResponse.of(역삼역)
                );
    }
}
