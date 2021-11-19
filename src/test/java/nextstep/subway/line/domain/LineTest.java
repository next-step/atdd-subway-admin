package nextstep.subway.line.domain;

import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.domain.StationTest.강남역;
import static nextstep.subway.station.domain.StationTest.역삼역;
import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @Test
    @DisplayName("구간을 포함한 노선 생성")
    void createLineAndSections(){

        Line line = new Line("2호선", "green");
        line.addSection(new Section(강남역, 역삼역, line, 10));

        assertThat(line.getStationResponses()).hasSize(2);
        assertThat(line.getStationResponses()).containsExactly(StationResponse.of(강남역), StationResponse.of(역삼역));
    }
}
