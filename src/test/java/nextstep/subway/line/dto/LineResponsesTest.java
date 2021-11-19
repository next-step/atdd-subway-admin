package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LineResponsesTest {
    @Test
    @DisplayName("노선 리스트 생성")
    public void createLinesResponseTest() {
        //given
        Line line1 = new Line("신분당선", "red", 강남역, 양재역, 7);
        Line line2 = new Line("2호선", "green", 강남역, 역삼역, 10);

        //when
        LineResponses linesResponse = LineResponses.of(Arrays.asList(line1, line2));

        //them
        assertThat(linesResponse).isEqualTo(LineResponses.of(Arrays.asList(line1, line2)));
    }
}
