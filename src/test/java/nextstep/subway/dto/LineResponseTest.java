package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineResponseTest {
    private Line line;

    @BeforeEach
    void setUp() {
        Section section = new Section(10, new Station("강남역"), new Station("잠실역"));
        line = new Line("2호선", "green", section);
    }

    @Test
    void 지하철_노선을_생성한다() {
        // when
        LineResponse result = LineResponse.of(line);
        // then
        assertThat(result.getStations()).hasSize(2);
    }
}